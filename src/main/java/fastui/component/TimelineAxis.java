package fastui.component;

import fastui.model.TimelineViewport;

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

    private static final long MS_DAY = 86_400_000L;
    private static final long MS_WEEK = 7 * MS_DAY;
    private static final long MS_MONTH = 30 * MS_DAY;
    private static final long MS_YEAR = 365 * MS_DAY;

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

    private enum EdgeDrag {NONE, LEFT, RIGHT, CENTER}

    private final Font font;
    private final Color backgroundColor;
    private final Color tickColor;
    private final Color labelColor;
    private final Color selectionIndicatorColor;
    private final TimelineViewport viewport;

    private EdgeDrag drag = EdgeDrag.NONE;
    private float dragStartX = 0f;
    private long dragViewStart = 0;
    private long dragViewEnd = 0;
    private long dragGrabTime = 0;

    public TimelineAxis(final TimelineViewport viewport,
                        final Font font, final Color backgroundColor,
                        final Color tickColor, final Color labelColor,
                        final Color selectionIndicatorColor) {
        this.viewport = viewport;
        this.font = font;
        this.backgroundColor = backgroundColor;
        this.tickColor = tickColor;
        this.labelColor = labelColor;
        this.selectionIndicatorColor = selectionIndicatorColor;

        this.viewport.addListener(new TimelineViewport.ViewportListener() {
            @Override
            public void onViewportChanged() {
                this.repaint();
            }

            @Override
            public void onSelectionChanged(final float min, final float max) {
                this.repaint();
            }

            private void repaint() {
                TimelineAxis.this.repaint();
            }
        });
    }

    @Override
    public void onMousePressed(final float mx, final float my) {
        final float third = this.width / 3f;
        if (mx <= this.x + third) {
            this.drag = EdgeDrag.LEFT;
        } else if (mx >= this.x + 2f * third) {
            this.drag = EdgeDrag.RIGHT;
        } else {
            this.drag = EdgeDrag.CENTER;
        }

        this.dragStartX = mx;
        this.dragViewStart = this.viewport.getViewStart();
        this.dragViewEnd = this.viewport.getViewEnd();

        final double fraction = (double) (mx - this.x) / Math.max(1f, this.width);
        this.dragGrabTime = this.viewport.getViewStart() + (long) (fraction * (this.viewport.getViewEnd() - this.viewport.getViewStart()));
    }

    @Override
    public void onMouseReleased(final float mx, final float my) {
        this.drag = EdgeDrag.NONE;
    }

    @Override
    public void onMouseDragged(final float mx, final float my) {
        if (this.drag == EdgeDrag.NONE || this.width <= 0) return;

        final double f = (double) (mx - this.x) / this.width;
        final long currentSpan = this.dragViewEnd - this.dragViewStart;

        if (this.drag == EdgeDrag.CENTER) {
            final long newStart = Math.max(this.viewport.getAbsoluteStart(), Math.min(this.dragGrabTime - (long) (f * currentSpan), this.viewport.getAbsoluteEnd() - currentSpan));
            this.viewport.setView(newStart, newStart + currentSpan);
        } else if (this.drag == EdgeDrag.LEFT) {
            double denom = 1.0 - f;
            if (denom < 0.05) denom = 0.05;
            final long newStart = (long) ((this.dragGrabTime - f * this.viewport.getViewEnd()) / denom);
            this.viewport.setView(Math.max(this.viewport.getAbsoluteStart(), Math.min(newStart, this.viewport.getViewEnd() - MS_DAY)), this.viewport.getViewEnd());
        } else if (this.drag == EdgeDrag.RIGHT) {
            double denom = f;
            if (denom < 0.05) denom = 0.05;
            final long newEnd = this.viewport.getViewStart() + (long) ((this.dragGrabTime - this.viewport.getViewStart()) / denom);
            this.viewport.setView(this.viewport.getViewStart(), Math.min(this.viewport.getAbsoluteEnd(), Math.max(newEnd, this.viewport.getViewStart() + MS_DAY)));
        }
    }

    @Override
    public void onRender(final Graphics2D g) {
        if (this.backgroundColor.getAlpha() > 0) {
            g.setColor(this.backgroundColor);
            g.fillRect((int) this.getAbsoluteX(), (int) this.getAbsoluteY(), (int) this.width, (int) this.height);
        }

        final Granularity gran = this.computeGranularity();
        final SimpleDateFormat fmt = new SimpleDateFormat(gran.format);
        g.setFont(this.font);
        final FontMetrics fm = g.getFontMetrics();
        final int tickH = 8;

        final Graphics2D g2 = (Graphics2D) g.create();
        g2.setClip((int) this.getAbsoluteX(), (int) this.getAbsoluteY(), (int) this.width, (int) this.height);

        for (final long tick : this.computeTicks()) {
            final float tx = this.toScreenX(tick);

            final String label = fmt.format(new Date(tick));
            final int labelW = fm.stringWidth(label);

            final float labelX = tx - labelW / 2f;

            g2.setColor(this.labelColor);
            g2.drawString(label, labelX, this.getAbsoluteY() + 6 + tickH + fm.getAscent() + 2);

            g2.setColor(this.tickColor);
            g2.drawLine((int) tx, (int) this.getAbsoluteY(), (int) tx, (int) (this.getAbsoluteY() + tickH));
        }

        g2.dispose();
    }

    public void onRangeChanged(final float min, final float max) {
        this.viewport.setSelection(min, max);
    }

    public float toScreenX(final long epochMs) {
        final long span = Math.max(1, this.viewport.getViewEnd() - this.viewport.getViewStart());
        final double fraction = (double) (epochMs - this.viewport.getViewStart()) / span;
        return this.getAbsoluteX() + (float) (fraction * this.width);
    }

    public List<Long> computeTicks() {
        final List<Long> ticks = new ArrayList<>();
        final Granularity gran = this.computeGranularity();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(this.viewport.getViewStart()));

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (gran == Granularity.WEEK) cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (gran == Granularity.MONTH || gran == Granularity.QUARTER) cal.set(Calendar.DAY_OF_MONTH, 1);
        if (gran == Granularity.QUARTER) cal.set(Calendar.MONTH, (cal.get(Calendar.MONTH) / 3) * 3);
        if (gran == Granularity.YEAR) cal.set(Calendar.DAY_OF_YEAR, 1);

        cal.add(gran.calField, -gran.calStep);

        while (cal.getTimeInMillis() <= this.viewport.getViewEnd() + (this.viewport.getViewEnd() - this.viewport.getViewStart()) / 5) {
            ticks.add(cal.getTimeInMillis());
            cal.add(gran.calField, gran.calStep);
            if (ticks.size() > 500) break;
        }
        return ticks;
    }

    private Granularity computeGranularity() {
        final long visibleMs = this.viewport.getViewEnd() - this.viewport.getViewStart();
        if (visibleMs <= MS_WEEK * 2) return Granularity.DAY;
        if (visibleMs <= MS_MONTH * 3) return Granularity.WEEK;
        if (visibleMs <= MS_YEAR) return Granularity.MONTH;
        if (visibleMs <= MS_YEAR * 3) return Granularity.QUARTER;
        return Granularity.YEAR;
    }

    public TimelineViewport getViewport() {
        return this.viewport;
    }
}
