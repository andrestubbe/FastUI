package fastui.composable;

import fastui.Factory;
import fastui.behaviour.Behaviour;
import fastui.behaviour.BehaviorEditable;
import fastui.component.Component;
import fastui.component.Image9Slice;
import fastui.component.TextDisplay;

import java.awt.*;
import java.util.function.Consumer;

public class TextArea implements Composable {
    private final Image9Slice background;
    private final TextDisplay display;

    public TextArea(float x, float y, float w, float h, int arc, Color bgColor, Font font, Color textColor) {
        this.background = new Image9Slice(arc, arc, arc, arc, Factory.createSliceableLayer((int) h, arc, bgColor));
        this.display = new TextDisplay(font, textColor);
        this.display.setMargin(12);
        this.display.addBehavior(new BehaviorEditable());
        this.setBounds(x, y, w, h);
    }

    public TextArea(float x, float y, float w, float h, int arc, int borderWidth, Color fillColor, Color borderColor, Font font, Color textColor) {
        this.background = new Image9Slice(arc, arc, arc, arc, Factory.createSliceableLayer((int) h, arc, borderWidth, fillColor, borderColor));
        this.display = new TextDisplay(font, textColor);
        this.display.setMargin(12);
        this.display.addBehavior(new BehaviorEditable());
        this.setBounds(x, y, w, h);
    }

    public TextArea(int arc, Color bgColor, Font font, Color textColor) {
        this(0, 0, 0, 0, arc, bgColor, font, textColor);
    }

    public void addBehavior(Behaviour behavior) {
        this.display.addBehavior(behavior);
    }

    public void append(String text) {
        this.display.append(text);
    }

    public void addChangeListener(Consumer<String> listener) {
        this.display.addChangeListener(listener);
    }

    public String getText() {
        return this.display.getText();
    }

    public float getPreferredHeight(float width) {
        return this.display.getPreferredHeight(width - 24) + 24;
    }

    @Override
    public void setMargin(float margin) {
        this.background.setMargin(margin);
    }

    @Override
    public void setMargin(float top, float left, float bottom, float right) {
        this.background.setMargin(top, left, bottom, right);
    }

    public void setBounds(float x, float y, float w, float h) {
        this.background.setBounds(x, y, w, h);
        float top = this.display.getMarginTop();
        float left = this.display.getMarginLeft();
        float bottom = this.display.getMarginBottom();
        float right = this.display.getMarginRight();
        this.display.setBounds(x + left, y + top, w - left - right, h - top - bottom);
    }

    public void setText(String text) {
        this.display.setText(text);
    }

    @Override
    public Component[] components() {
        return new Component[]{background, display};
    }
}
