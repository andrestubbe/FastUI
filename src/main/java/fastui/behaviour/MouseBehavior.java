package fastui.behaviour;

import fastui.component.Component;
import java.awt.event.KeyEvent;

public interface MouseBehavior {
    default void onMousePressed(Component target, int mx, int my) {}
    default void onMouseReleased(Component target, int mx, int my) {}
    default void onMouseMoved(Component target, int mx, int my) {}
    default void onMouseDragged(Component target, int mx, int my) {}
    default void onMouseEnter(Component target) {}
    default void onMouseExit(Component target) {}
    default void onFocusGained(Component target) {}
    default void onFocusLost(Component target) {}
    default void onKeyPressed(Component target, KeyEvent e) {}
    default void onKeyTyped(Component target, KeyEvent e) {}
    default void onKeyReleased(Component target, KeyEvent e) {}
}
