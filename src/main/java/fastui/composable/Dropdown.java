package fastui.composable;

import fastui.Factory;
import fastui.component.Component;
import fastui.component.Image9Slice;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Dropdown implements Composable {
    private final Image9Slice background;
    private final List<Component> children = new ArrayList<>();

    public Dropdown(float x, float y, float w, float h, int arc, int border, Color fillColor, Color borderColor) {
        this.background = new Image9Slice(arc, arc, arc, arc, Factory.createSliceableLayer((int) h, arc, border, fillColor, borderColor));
        this.setBounds(x, y, w, h);
    }

    public void add(Component component) {
        this.children.add(component);
    }

    public void add(Composable composable) {
        for (Component c : composable.components()) {
            this.add(c);
        }
    }

    public void setBounds(float x, float y, float w, float h) {
        this.background.setBounds(x, y, w, h);
    }

    @Override
    public Component[] components() {
        List<Component> all = new ArrayList<>();
        all.add(background);
        all.addAll(children);
        return all.toArray(new Component[0]);
    }
}
