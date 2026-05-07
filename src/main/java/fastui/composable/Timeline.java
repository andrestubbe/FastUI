package fastui.composable;

import fastui.Factory;
import fastui.component.Component;
import fastui.component.TimelineAxis;
import fastui.component.Image3x3;
import fastui.behaviour.RangeBehavior3x3;
import fastui.behaviour.MouseBehavior;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Timeline implements Composable {
    private final Image3x3 mainBackground;
    private final TimelineAxis axis;
    private final Range range;
    private final TimelineGrid grid;
    
    public Timeline(final long start, final long end,
                    final int totalH, final int arc, final Color bgColor,
                    final int rangeH, final Color rangeTrackColor, final Color rangeSpanColor,
                    final Font font, final Color tick, final Color label) {
        
        // 1. Shared background
        this.mainBackground = new Image3x3(Factory.createSliceableLayer(totalH, arc, bgColor));
        
        // 2. Components
        final int rangeArc = rangeH / 4;
        final BufferedImage transparentTrack = Factory.createSliceableLayer(rangeH, rangeArc, new Color(0,0,0,0));
        final BufferedImage rangeSpanImg = Factory.createSliceableLayer(rangeH, rangeArc, rangeSpanColor);

        this.range = new Range(transparentTrack, rangeSpanImg, 0.2f, 0.8f);
        this.axis = new TimelineAxis(start, end, font.deriveFont(22f), new Color(0,0,0,0), tick, label, new Color(0,0,0,0));
        
        // 3. Timeline Grid & Selection Overlay
        this.grid = new TimelineGrid(this.axis, tick);
        this.grid.setHitTestable(false);

        // GLOBAL PANNING
        this.mainBackground.addBehavior(new MouseBehavior() {
            private int lastX;
            @Override
            public void onMousePressed(Component target, int mx, int my) { lastX = mx; }
            @Override
            public void onMouseDragged(Component target, int mx, int my) {
                int dx = mx - lastX;
                long msPerPx = (axis.getViewEnd() - axis.getViewStart()) / Math.max(1, axis.getWidth());
                axis.pan(-dx * msPerPx);
                lastX = mx;
            }
        });

        // Wiring
        this.range.getBehavior().addListener((min, max) -> {
            this.axis.onRangeChanged(min, max);
            this.grid.updateSelection(min, max);
        });

        // Initial Sync
        this.grid.updateSelection(this.range.getMinValue(), this.range.getMaxValue());
        this.axis.onRangeChanged(this.range.getMinValue(), this.range.getMaxValue());

        final long now = System.currentTimeMillis();
        final long sevenDaysMs = 7L * 24 * 60 * 60 * 1000;
        this.axis.setViewport(now - sevenDaysMs, now);
    }

    public RangeBehavior3x3 getRangeBehavior() { return range.getBehavior(); }

    public void setBounds(final int x, final int y, final int w, final int axisH, final int rangeH, final int gap) {
        int padding = 10;
        this.mainBackground.setBounds(x, y, w, axisH + rangeH + (padding * 2));
        this.axis.setBounds(x + padding, y + padding, w - (padding * 2), axisH);
        this.range.setBounds(x + padding, y + padding + axisH, w - (padding * 2), rangeH);
        
        // Grid stores the split height to know where the axis ends
        this.grid.setSplitY(axisH);
        this.grid.setBounds(x + padding, y + padding, w - (padding * 2), axisH + rangeH);
    }

    @Override
    public fastui.component.Component[] components() {
        List<fastui.component.Component> all = new ArrayList<>();
        all.add(this.mainBackground);
        all.add(this.axis);
        for (fastui.component.Component c : this.range.components()) all.add(c);
        all.add(this.grid);
        return all.toArray(new fastui.component.Component[0]);
    }

    private static class TimelineGrid extends fastui.component.Component {
        private final TimelineAxis axis;
        private final Color gridColor;
        private float selMin, selMax;
        private int axisH;
        private final SimpleDateFormat timeFmt = new SimpleDateFormat("dd.MM HH:mm");

        public TimelineGrid(TimelineAxis axis, Color gridColor) {
            this.axis = axis;
            this.gridColor = new Color(gridColor.getRed(), gridColor.getGreen(), gridColor.getBlue(), 196); 
        }

        public void setSplitY(int axisH) { this.axisH = axisH; }

        public void updateSelection(float min, float max) {
            this.selMin = min;
            this.selMax = max;
            repaint();
        }

        @Override
        public void render(Graphics2D g) {
            // 1. Draw Time Grid (Continuous)
            g.setColor(this.gridColor);
            g.setStroke(new BasicStroke(1f));
            for (long tick : axis.computeTicks()) {
                int tx = this.x + (int)(((float)(tick - axis.getViewStart()) / (axis.getViewEnd() - axis.getViewStart())) * this.width);
                if (tx >= this.x && tx <= this.x + this.width) {
                    g.drawLine(tx, this.y, tx, this.y + this.height);
                }
            }

            // 2. Draw Selection Markers (Only in Axis area + Label)
            long span = axis.getViewEnd() - axis.getViewStart();
            long tMin = axis.getViewStart() + (long)(selMin * span);
            long tMax = axis.getViewStart() + (long)(selMax * span);

            renderMarker(g, selMin, tMin, true);
            renderMarker(g, selMax, tMax, false);
        }

        private void renderMarker(Graphics2D g, float relPos, long time, boolean isLeftMarker) {
            int tx = this.x + (int)(relPos * this.width);
            
            // Marker line (only in axis area)
            g.setColor(new Color(32, 255, 128)); // Full Neon Green
            g.setStroke(new BasicStroke(2f));
            g.drawLine(tx, this.y, tx, this.y + this.axisH);
            
            // Time Label (Always on the INNER side of the selection)
            g.setFont(new Font("Inter", Font.BOLD, 14));
            String label = timeFmt.format(new Date(time));
            int labelW = g.getFontMetrics().stringWidth(label);
            
            // If it's the LEFT marker, draw to the RIGHT (inside).
            // If it's the RIGHT marker, draw to the LEFT (inside).
            int lx = isLeftMarker ? tx + 8 : tx - labelW - 8;
            
            g.setColor(new Color(32, 255, 128));
            g.drawString(label, lx, this.y + 20);
        }
    }
}
