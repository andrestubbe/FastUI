package fastui.composable;

import fastui.component.Component;

public interface Composable {
    Component[] components();

    default void setBounds(float x, float y, float w, float h) {}

    default void setMargin(float margin) {
        for (Component c : components()) {
            c.setMargin(margin);
        }
    }

    default void setMargin(float top, float left, float bottom, float right) {
        for (Component c : components()) {
            c.setMargin(top, left, bottom, right);
        }
    }

    default float getMarginTop() {
        Component[] comps = components();
        return comps.length > 0 ? comps[0].getMarginTop() : 0;
    }

    default float getMarginLeft() {
        Component[] comps = components();
        return comps.length > 0 ? comps[0].getMarginLeft() : 0;
    }

    default float getMarginBottom() {
        Component[] comps = components();
        return comps.length > 0 ? comps[0].getMarginBottom() : 0;
    }

    default float getMarginRight() {
        Component[] comps = components();
        return comps.length > 0 ? comps[0].getMarginRight() : 0;
    }

    default float getPreferredWidth() {
        return 0;
    }
}
