package fastui.behaviour;

import fastui.component.Component;
import fastui.component.Sliceable;

import java.awt.image.BufferedImage;

public class BehaviorButton3x3 implements Behaviour {
    private final BufferedImage base;
    private final BufferedImage hover;
    private final BufferedImage pressed;

    public BehaviorButton3x3(final BufferedImage base, final BufferedImage hover, final BufferedImage pressed) {
        this.base = base;
        this.hover = hover;
        this.pressed = pressed;
    }

    @Override
    public void onMouseEnter(final Component target) {
        ((Sliceable) target).setImages(this.hover);
    }

    @Override
    public void onMouseExit(final Component target) {
        ((Sliceable) target).setImages(this.base);
    }

    @Override
    public void onMousePressed(final Component target, final float mx, final float my) {
        ((Sliceable) target).setImages(this.pressed);
    }

    @Override
    public void onMouseReleased(final Component target, final float mx, final float my) {
        ((Sliceable) target).setImages(this.base);
    }
}
