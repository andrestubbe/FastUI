package fastui.composable;

import fastui.Factory;
import fastui.behaviour.BehaviorButton3x3;
import fastui.behaviour.Behaviour;
import fastui.component.Component;
import fastui.component.Image;
import fastui.component.Image9Slice;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button implements Composable {

    private final Image9Slice background;
    private final Image label;
    private final float labelW;
    private final float labelH;

    public Button(
            final float x, final float y,
            final float width, final float height,
            final ButtonTheme theme,
            final String text, final Font font, final Color textColor
    ) {
        this(x, y, width, height, theme.arc, theme.base, theme.hover, theme.pressed, text, font, textColor);
    }

    public Button(
            final float x, final float y,
            final float width, final float height,
            final int arc,
            final Color baseColor,
            final String text, final Font font, final Color textColor
    ) {
        this(x, y, width, height, arc, baseColor, baseColor.brighter(), baseColor.darker(), text, font, textColor);
    }

    public Button(final float x, final float y, final float width, final float height, final int arc, final Color baseColor, final Color hoverColor, final Color pressedColor, final String text, final Font font, final Color textColor) {

        final BufferedImage base = Factory.createSliceableLayer((int) height, arc, baseColor);
        final BufferedImage hover = Factory.createSliceableLayer((int) height, arc, hoverColor);
        final BufferedImage pressed = Factory.createSliceableLayer((int) height, arc, pressedColor);

        int slice = arc / 2;
        this.background = new Image9Slice(slice, slice, slice, slice, base);
        this.background.addBehavior(new BehaviorButton3x3(base, hover, pressed));

        final BufferedImage bakedLabel = Factory.createLabel(text, font, textColor);
        this.label = new Image(bakedLabel);
        this.label.setHitTestable(false);
        this.labelW = (float) bakedLabel.getWidth();
        this.labelH = (float) bakedLabel.getHeight();

        if (width > 0) {
            this.setBounds(x, y, width, height);
        }
    }

    public void setBounds(final float x, final float y, final float width, final float height) {
        this.background.setBounds(x, y, width, height);

        final float lx = x + (width - this.labelW) / 2f;
        final float ly = y + (height - this.labelH) / 2f;
        this.label.setBounds(lx, ly, this.labelW, this.labelH);
    }

    public void addBehavior(Behaviour behavior) {
        this.background.addBehavior(behavior);
    }

    @Override
    public Component[] components() {
        return new Component[]{this.background, this.label};
    }
}
