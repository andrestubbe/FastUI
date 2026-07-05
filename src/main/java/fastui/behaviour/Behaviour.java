package fastui.behaviour;

import fastui.component.Component;

import java.awt.event.KeyEvent;

public interface Behaviour {

    default void onMousePressed(Component target, float mx, float my) {
    }

    default void onMouseReleased(Component target, float mx, float my) {
    }

    default void onMouseMoved(Component target, float mx, float my) {
    }

    default void onMouseDragged(Component target, float mx, float my) {
    }

    default void onMouseEnter(Component target) {
    }

    default void onMouseExit(Component target) {
    }

    default void onFocusGained(Component target) {
    }

    default void onFocusLost(Component target) {
    }

    default void onKeyPressed(Component target, KeyEvent e) {
    }

    default void onKeyTyped(Component target, KeyEvent e) {
    }

    default void onKeyReleased(Component target, KeyEvent e) {
    }

    default void onRender(Component target, java.awt.Graphics2D g) {
    }
}
