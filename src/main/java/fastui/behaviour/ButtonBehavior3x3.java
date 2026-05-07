package fastui.behaviour;

import fastui.component.Component;
import fastui.component.Image3x3;

import java.awt.image.BufferedImage;

public class ButtonBehavior3x3 implements MouseBehavior {
    private final BufferedImage base;
    private final BufferedImage hover;
    private final BufferedImage pressed;

    public ButtonBehavior3x3(final BufferedImage base, final BufferedImage hover, final BufferedImage pressed) {
        this.base = base;
        this.hover = hover;
        this.pressed = pressed;
    }

    @Override
    public void onMouseEnter(final Component target) {
        ((Image3x3) target).setImages(this.hover);
    }

    @Override
    public void onMouseExit(final Component target) {
        ((Image3x3) target).setImages(this.base);
    }

    @Override
    public void onMousePressed(final Component target, final int mx, final int my) {
        ((Image3x3) target).setImages(this.pressed);
    }

    @Override
    public void onMouseReleased(final Component target, final int mx, final int my) {
        ((Image3x3) target).setImages(this.base);
    }
}
