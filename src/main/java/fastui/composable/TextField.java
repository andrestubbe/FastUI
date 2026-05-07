package fastui.composable;

import fastui.Factory;
import fastui.component.Image3x3;
import fastui.component.Text;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextField implements Composable {
    private final Image3x3 background;
    private final Text text;

    public TextField(final int height, final int arc, final Color bgColor, 
                     final Font font, final Color textColor, final Color cursorColor) {
        
        final BufferedImage bgImg = Factory.createSliceableLayer(height, arc, bgColor);
        this.background = new Image3x3(bgImg);
        this.text = new Text(font, textColor, cursorColor);
    }

    public void setBounds(final int x, final int y, final int width, final int height) {
        this.background.setBounds(x, y, width, height);
        this.text.setBounds(x, y, width, height);
    }

    public void setText(final String value) {
        this.text.setText(value);
    }

    public String getText() {
        return this.text.getText();
    }

    @Override
    public fastui.component.Component[] components() {
        return new fastui.component.Component[]{this.background, this.text};
    }
}
