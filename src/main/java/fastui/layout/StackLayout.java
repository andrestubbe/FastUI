package fastui.layout;

import fastui.component.Component;

public class StackLayout implements Layout {
    public static final StackLayout INSTANCE = new StackLayout();

    private StackLayout() {}

    @Override
    public void apply(Component parent) {
        Component child = parent.firstChild;
        while (child != null) {
            if (child.isVisible()) {
                child.setBounds(
                    parent.getPaddingLeft() + child.marginLeft,
                    parent.getPaddingTop() + child.marginTop,
                    parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight() - child.marginLeft - child.marginRight,
                    parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom() - child.marginTop - child.marginBottom
                );
            }
            child = child.nextSibling;
        }
    }
}
