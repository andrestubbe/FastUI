package fastui.layout;

import fastui.component.Component;
import java.util.List;

public interface LayoutManager {
    void layout(Component parent, List<Component> children);
}
