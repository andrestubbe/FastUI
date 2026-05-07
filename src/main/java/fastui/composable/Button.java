package fastui.composable;

import fastui.Factory;
import fastui.behaviour.ButtonBehavior3x3;
import fastui.component.Component;
import fastui.component.Image;
import fastui.component.Image3x3;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

public class Button implements Composable {
    private final Image3x3 background;
    private final Image label;
    private final int labelW;
    private final int labelH;

    public Button(final int height, final int arc, final Color baseColor,
                  final String text, final Font font, final Color textColor) {
        
        final BufferedImage base = Factory.createSliceableLayer(height, arc, baseColor);
        final BufferedImage hover = Factory.createSliceableLayer(height, arc, baseColor.brighter());
        final BufferedImage pressed = Factory.createSliceableLayer(height, arc, baseColor.darker());
        
        this.background = new Image3x3(base);
        this.background.addBehavior(new ButtonBehavior3x3(base, hover, pressed));
        
        final BufferedImage bakedLabel = Factory.createLabel(text, font, textColor);
        this.label = new Image(bakedLabel);
        this.labelW = bakedLabel.getWidth();
        this.labelH = bakedLabel.getHeight();
    }

    public void setBounds(final int x, final int y, final int width, final int height) {
        this.background.setBounds(x, y, width, height);
        
        final int lx = x + (width - this.labelW) / 2;
        final int ly = y + (height - this.labelH) / 2;
        this.label.setBounds(lx, ly, this.labelW, this.labelH);
    }

    @Override
    public Component[] components() {
        return new Component[]{this.background, this.label};
    }
}
