package fastui.component;

import fastui.model.TimelineViewport;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimelineGrid extends Component {

    private final TimelineAxis axis;
    private final TimelineViewport viewport;
    private final Color gridColor;
    private final SimpleDateFormat timeFmt = new SimpleDateFormat("dd.MM HH:mm");

    private int axisH;

    public TimelineGrid(final TimelineAxis axis, final TimelineViewport viewport, final Color gridColor) {
        this.axis = axis;
        this.viewport = viewport;
        this.gridColor = new Color(gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue(), 196);

        this.viewport.addListener(new TimelineViewport.ViewportListener() {
            @Override
            public void onViewportChanged() {
                TimelineGrid.this.repaint();
            }

            @Override
            public void onSelectionChanged(final float min, final float max) {
                TimelineGrid.this.repaint();
            }
        });
    }

    @Override
    public void onRender(final Graphics2D g) {
        g.setColor(this.gridColor);
        g.setStroke(new BasicStroke(1f));

        for (final long tick : this.axis.computeTicks()) {
            final float tx = this.axis.toScreenX(tick);
            if (tx >= this.getAbsoluteX() && tx <= this.getAbsoluteX() + this.width) {
                g.drawLine((int) tx, (int) this.getAbsoluteY(), (int) tx, (int) (this.getAbsoluteY() + this.height));
            }
        }

        final long span = this.viewport.getViewEnd() - this.viewport.getViewStart();
        final long tMin = this.viewport.getViewStart() + (long) (this.viewport.getSelectionMin() * span);
        final long tMax = this.viewport.getViewStart() + (long) (this.viewport.getSelectionMax() * span);

        this.renderMarker(g, this.viewport.getSelectionMin(), tMin, true);
        this.renderMarker(g, this.viewport.getSelectionMax(), tMax, false);
    }

    private void renderMarker(final Graphics2D g, final float relPos, final long time, final boolean isLeftMarker) {
        final float tx = this.getAbsoluteX() + (relPos * this.width);

        g.setColor(new Color(32, 255, 128));
        g.setStroke(new BasicStroke(2f));
        g.drawLine((int) tx, (int) this.getAbsoluteY(), (int) tx, (int) (this.getAbsoluteY() + this.axisH));

        g.setFont(new Font("Inter", Font.BOLD, 14));
        final String label = this.timeFmt.format(new Date(time));
        final int labelW = g.getFontMetrics().stringWidth(label);

        final float lx = isLeftMarker ? tx + 8 : tx - labelW - 8;
        g.setColor(new Color(32, 255, 128));
        g.drawString(label, lx, this.getAbsoluteY() + 20);
    }

    public void setSplitY(final int axisH) {
        this.axisH = axisH;
    }
}
