package fastui.layout;

import fastui.component.Component;

public class NoneLayout implements Layout {
    public static final NoneLayout INSTANCE = new NoneLayout();

    private NoneLayout() {}

    @Override
    public void apply(Component parent) {
        // Absolute positioning
    }
}
