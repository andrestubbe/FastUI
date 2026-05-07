package fastui.composable;

import fastui.component.Component;
import fastui.behaviour.RangeBehavior3x3;
import fastui.component.Image3x3;

import java.awt.image.BufferedImage;

public class Range implements Composable {
    private final Image3x3 track;
    private final Image3x3 bar;
    private final RangeBehavior3x3 behavior;

    public Range(final BufferedImage trackImg, final BufferedImage spanImg,
                 final float minValue, final float maxValue) {
        this.track = new Image3x3(trackImg);
        this.bar = new Image3x3(spanImg);
        this.behavior = new RangeBehavior3x3(minValue, maxValue);
        this.bar.setRange(minValue, maxValue);
        this.bar.addBehavior(this.behavior);
    }

    public void setBounds(final int x, final int y, final int width, final int height) {
        this.track.setBounds(x, y, width, height);
        this.bar.setBounds(x, y, width, height);
    }

    @Override
    public Component[] components() {
        return new Component[]{this.track, this.bar};
    }

    public RangeBehavior3x3 getBehavior() { return this.behavior; }
    public float getMinValue() { return this.behavior.getMinValue(); }
    public float getMaxValue() { return this.behavior.getMaxValue(); }
}
