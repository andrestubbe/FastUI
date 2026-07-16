package fastui.composable;

import fastui.Factory;
import fastui.component.Image9Slice;
import fastui.component.Text;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TextField implements Composable {
    private final Image9Slice background;
    private final Text text;

    public TextField(final float x, final float y, final float width, final float height, final int arc, final Color bgColor, 
                     final Font font, final Color textColor, final Color cursorColor) {
        
        final BufferedImage bgImg = Factory.createSliceableLayer((int)height, arc, bgColor);
        this.background = new Image9Slice(arc, arc, arc, arc, bgImg);
        this.text = new Text(font, textColor, cursorColor);
        this.setBounds(x, y, width, height);
    }

    public TextField(final float height, final int arc, final Color bgColor, 
                     final Font font, final Color textColor, final Color cursorColor) {
        this(0, 0, 0, height, arc, bgColor, font, textColor, cursorColor);
    }

    public void addChangeListener(java.util.function.Consumer<String> listener) {
        this.text.addChangeListener(listener);
    }

    public void setBounds(final float x, final float y, final float width, final float height) {
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
