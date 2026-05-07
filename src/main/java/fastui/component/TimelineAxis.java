package fastui.component;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimelineAxis extends Component {
    public interface ViewportListener {
        void onViewportChanged();
    }

    private static final long MS_DAY   = 86_400_000L;
    private static final long MS_WEEK  = 7 * MS_DAY;
    private static final long MS_MONTH = 30 * MS_DAY;
    private static final long MS_YEAR  = 365 * MS_DAY;

    private enum Granularity {
        DAY("dd MMM", Calendar.DAY_OF_MONTH, 1),
        WEEK("dd MMM", Calendar.WEEK_OF_YEAR, 1),
        MONTH("MMM yyyy", Calendar.MONTH, 1),
        QUARTER("MMM yyyy", Calendar.MONTH, 3),
        YEAR("yyyy", Calendar.YEAR, 1);

        final String format;
        final int calField;
        final int calStep;

        Granularity(final String fmt, final int calField, final int calStep) {
            this.format = fmt;
            this.calField = calField;
            this.calStep = calStep;
        }
    }

    private enum EdgeDrag { NONE, LEFT, RIGHT, CENTER }

    private final Font font;
    private final Color backgroundColor;
    private final Color tickColor;
    private final Color labelColor;
    private final Color selectionIndicatorColor;

    private final long absoluteStart;
    private final long absoluteEnd;

    private long viewStart;
    private long viewEnd;

    private float relSelMin = 0.2f;
    private float relSelMax = 0.8f;

    private EdgeDrag drag = EdgeDrag.NONE;
    private int dragStartX = 0;
    private long dragViewStart = 0;
    private long dragViewEnd = 0;
    private long dragGrabTime = 0;

    private final List<ViewportListener> viewportListeners = new ArrayList<>();

    public TimelineAxis(final long absoluteStart, final long absoluteEnd,
                        final Font font, final Color backgroundColor,
                        final Color tickColor, final Color labelColor,
                        final Color selectionIndicatorColor) {
        this.absoluteStart = absoluteStart;
        this.absoluteEnd = absoluteEnd;
        this.viewStart = absoluteStart;
        this.viewEnd = absoluteEnd;
        this.font = font;
        this.backgroundColor = backgroundColor;
        this.tickColor = tickColor;
        this.labelColor = labelColor;
        this.selectionIndicatorColor = selectionIndicatorColor;
    }

    public void onRangeChanged(final float min, final float max) {
        this.relSelMin = min;
        this.relSelMax = max;
        this.repaint();
    }

    public void setViewport(final long start, final long end) {
        this.viewStart = Math.max(this.absoluteStart, start);
        this.viewEnd = Math.min(this.absoluteEnd, end);
        this.notifyViewport();
    }

    public void pan(final long deltaMs) {
        final long currentSpan = this.viewEnd - this.viewStart;
        long newStart = Math.max(this.absoluteStart, Math.min(this.viewStart + deltaMs, this.absoluteEnd - currentSpan));
        this.viewStart = newStart;
        this.viewEnd = newStart + currentSpan;
        this.notifyViewport();
    }

    public long getAbsoluteStart() { return this.absoluteStart; }
    public long getAbsoluteEnd() { return this.absoluteEnd; }
    public long getViewStart() { return this.viewStart; }
    public long getViewEnd() { return this.viewEnd; }

    public void addViewportListener(final ViewportListener l) {
        this.viewportListeners.add(l);
    }

    private void notifyViewport() {
        for (final ViewportListener l : this.viewportListeners) l.onViewportChanged();
        this.repaint();
    }

    @Override
    public void onMousePressed(final int mx, final int my) {
        final int third = this.width / 3;
        if (mx <= this.x + third) {
            this.drag = EdgeDrag.LEFT;
        } else if (mx >= this.x + 2 * third) {
            this.drag = EdgeDrag.RIGHT;
        } else {
            this.drag = EdgeDrag.CENTER;
        }
        
        this.dragStartX = mx;
        this.dragViewStart = this.viewStart;
        this.dragViewEnd = this.viewEnd;
        
        final double fraction = (double)(mx - this.x) / Math.max(1, this.width);
        this.dragGrabTime = this.viewStart + (long)(fraction * (this.viewEnd - this.viewStart));
    }

    @Override
    public void onMouseReleased(final int mx, final int my) {
        this.drag = EdgeDrag.NONE;
    }

    @Override
    public void onMouseDragged(final int mx, final int my) {
        if (this.drag == EdgeDrag.NONE || this.width <= 0) return;
        
        final double f = (double)(mx - this.x) / this.width;
        final long currentSpan = this.dragViewEnd - this.dragViewStart;

        if (this.drag == EdgeDrag.CENTER) {
            long newStart = Math.max(this.absoluteStart, Math.min(this.dragGrabTime - (long)(f * currentSpan), this.absoluteEnd - currentSpan));
            this.viewStart = newStart;
            this.viewEnd = newStart + currentSpan;
        } else if (this.drag == EdgeDrag.LEFT) {
            double denom = 1.0 - f;
            if (denom < 0.05) denom = 0.05; 
            long newStart = (long)((this.dragGrabTime - f * this.viewEnd) / denom);
            this.viewStart = Math.max(this.absoluteStart, Math.min(newStart, this.viewEnd - MS_DAY));
        } else if (this.drag == EdgeDrag.RIGHT) {
            double denom = f;
            if (denom < 0.05) denom = 0.05;
            long newEnd = this.viewStart + (long)((this.dragGrabTime - this.viewStart) / denom);
            this.viewEnd = Math.min(this.absoluteEnd, Math.max(newEnd, this.viewStart + MS_DAY));
        }
        this.notifyViewport();
    }

    private Granularity computeGranularity() {
        final long visibleMs = this.viewEnd - this.viewStart;
        if (visibleMs <= MS_WEEK * 2)  return Granularity.DAY;
        if (visibleMs <= MS_MONTH * 3) return Granularity.WEEK;
        if (visibleMs <= MS_YEAR)      return Granularity.MONTH;
        if (visibleMs <= MS_YEAR * 3)  return Granularity.QUARTER;
        return Granularity.YEAR;
    }

    public int toScreenX(final long epochMs) {
        final long span = Math.max(1, this.viewEnd - this.viewStart);
        final double fraction = (double)(epochMs - this.viewStart) / span;
        return this.x + (int)(fraction * this.width);
    }

    public List<Long> computeTicks() {
        final List<Long> ticks = new ArrayList<>();
        final Granularity gran = this.computeGranularity();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(this.viewStart));
        
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        if (gran == Granularity.WEEK) cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (gran == Granularity.MONTH || gran == Granularity.QUARTER) cal.set(Calendar.DAY_OF_MONTH, 1);
        if (gran == Granularity.QUARTER) cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) / 3) * 3);
        if (gran == Granularity.YEAR) cal.set(Calendar.DAY_OF_YEAR, 1);

        while (cal.getTimeInMillis() <= this.viewEnd) {
            if (cal.getTimeInMillis() >= this.viewStart) ticks.add(cal.getTimeInMillis());
            cal.add(gran.calField, gran.calStep);
            if (ticks.size() > 500) break;
        }
        return ticks;
    }

    @Override
    public void render(final Graphics2D g) {
        if (this.backgroundColor.getAlpha() > 0) {
            g.setColor(this.backgroundColor);
            g.fillRect(this.x, this.y, this.width, this.height);
        }

        final Granularity gran = this.computeGranularity();
        final SimpleDateFormat fmt = new SimpleDateFormat(gran.format);
        g.setFont(this.font);
        final FontMetrics fm = g.getFontMetrics();
        final int tickH = 8;

        for (final long tick : this.computeTicks()) {
            final int tx = this.toScreenX(tick);
            if (tx < this.x || tx > this.x + this.width) continue;
            
            final String label = fmt.format(new Date(tick));
            final int labelW = fm.stringWidth(label);
            final int labelX = Math.max(this.x, Math.min(tx - labelW / 2, this.x + this.width - labelW));
            g.setColor(this.labelColor);
            g.drawString(label, labelX, this.y + 6 + tickH + fm.getAscent() + 2);
        }
    }
}
