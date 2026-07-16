package fastui.composable;

import fastui.Factory;
import fastui.component.Component;
import fastui.component.TimelineAxis;
import fastui.component.TimelineGrid;
import fastui.component.Image9Slice;
import fastui.model.TimelineViewport;
import fastui.behaviour.BehaviorRange3x3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Timeline implements Composable {

    private final TimelineViewport viewport;
    private final Image9Slice mainBackground;
    private final TimelineAxis axis;
    private final Range range;
    private final TimelineGrid grid;

    private float axisH;
    private float rangeH;
    private float gap;

    public Timeline(final float x, final float y, final float w, final float axisH, final float rangeH, final float gap,
                    final long start, final long end,
                    final int arc, final Color bgColor,
                    final Color rangeTrackColor, final Color rangeSpanColor,
                    final Font font, final Color tick, final Color label) {
        
        this.axisH = axisH;
        this.rangeH = rangeH;
        this.gap = gap;

        this.viewport = new TimelineViewport(start, end);
        
        final float totalH = axisH + rangeH + (10f * 2f); 
        this.mainBackground = new Image9Slice(arc, arc, arc, arc, Factory.createSliceableLayer((int)totalH, arc, bgColor));
        
        this.axis = new TimelineAxis(this.viewport, font.deriveFont(22f), new Color(0,0,0,0), tick, label, new Color(0,0,0,0));
        
        final int rangeArc = (int)rangeH / 4;
        final BufferedImage transparentTrack = Factory.createSliceableLayer((int)rangeH, rangeArc, new Color(0,0,0,0));
        final BufferedImage rangeSpanImg = Factory.createSliceableLayer((int)rangeH, rangeArc, rangeSpanColor);
        this.range = new Range(transparentTrack, rangeSpanImg, 0.2f, 0.8f);
        
        this.range.getBehavior().addListener((min, max) -> {
            this.viewport.setSelection(min, max);
        });
        
        this.grid = new TimelineGrid(this.axis, this.viewport, tick);
        this.grid.setHitTestable(false);

        this.setBounds(x, y, w, axisH, rangeH, gap);

        final long now = System.currentTimeMillis();
        final long sevenDaysMs = 7L * 24 * 60 * 60 * 1000;
        this.viewport.setView(now - sevenDaysMs, now);
        this.viewport.setSelection(0.2f, 0.8f);
    }

    public void setBounds(final float x, final float y, final float w, final float axisH, final float rangeH, final float gap) {
        final float padding = 10f;
        this.mainBackground.setBounds(x, y, w, axisH + rangeH + (padding * 2f));
        this.axis.setBounds(x + padding, y + padding, w - (padding * 2f), axisH);
        this.range.setBounds(x + padding, y + padding + axisH, w - (padding * 2f), rangeH);
        this.grid.setSplitY((int)axisH);
        this.grid.setBounds(x + padding, y + padding, w - (padding * 2f), axisH + rangeH);
    }

    @Override
    public Component[] components() {
        final List<Component> all = new ArrayList<>();
        all.add(this.mainBackground);
        all.add(this.axis);
        for (final Component c : this.range.components()) all.add(c);
        all.add(this.grid);
        return all.toArray(new Component[0]);
    }

    public TimelineViewport getViewport() { return this.viewport; }
    public BehaviorRange3x3 getRangeBehavior() { return this.range.getBehavior(); }
}
