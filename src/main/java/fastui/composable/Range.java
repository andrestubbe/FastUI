package fastui.composable;

import fastui.behaviour.BehaviorRange3x3;
import fastui.component.Component;
import fastui.component.Image9Slice;

import java.awt.image.BufferedImage;

public class Range implements Composable {

    private final Image9Slice track;
    private final Image9Slice bar;
    private final BehaviorRange3x3 behavior;

    private float x;
    private float y;
    private float width;
    private float height;

    public Range(final float x, final float y, final float width, final float height,
                 final BufferedImage trackImg, final BufferedImage spanImg,
                 final float minValue, final float maxValue) {

        final int arc = trackImg.getHeight() / 4;
        this.track = new Image9Slice(arc, arc, arc, arc, trackImg);
        this.bar = new Image9Slice(arc, arc, arc, arc, spanImg);
        this.bar.setHitTestable(false);
        this.behavior = new BehaviorRange3x3(minValue, maxValue);

        this.track.addBehavior(this.behavior);

        this.behavior.addListener((min, max) -> {
            this.updateBarBounds(min, max);
        });

        this.setBounds(x, y, width, height);
        this.updateBarBounds(minValue, maxValue);
    }

    public Range(final BufferedImage trackImg, final BufferedImage spanImg,
                 final float minValue, final float maxValue) {
        this(0, 0, 0, 0, trackImg, spanImg, minValue, maxValue);
    }

    public void setBounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.track.setBounds(x, y, width, height);
        this.updateBarBounds(this.behavior.getMinValue(), this.behavior.getMaxValue());
    }

    @Override
    public Component[] components() {
        return new Component[]{this.track, this.bar};
    }

    private void updateBarBounds(final float min, final float max) {
        if (this.width <= 0) return;
        final float bx = this.x + (min * this.width);
        final float bw = ((max - min) * this.width);
        this.bar.setBounds(bx, this.y, bw, this.height);
    }

    public BehaviorRange3x3 getBehavior() {
        return this.behavior;
    }

    public float getMinValue() {
        return this.behavior.getMinValue();
    }

    public float getMaxValue() {
        return this.behavior.getMaxValue();
    }
}
