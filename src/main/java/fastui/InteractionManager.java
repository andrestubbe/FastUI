package fastui;

import fastui.component.Component;
import java.awt.event.*;

public class InteractionManager {

    private final Container container;
    private final java.awt.Component parent;

    private Component hovered = null;
    private Component active = null;
    private Component focused = null;

    public InteractionManager(final Container container, final java.awt.Component parent) {
        this.container = container;
        this.parent = parent;
    }

    public MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                InteractionManager.this.parent.requestFocusInWindow();
                final Component hit = InteractionManager.this.findComponentAt(e.getX(), e.getY());
                
                if (hit != InteractionManager.this.focused) {
                    if (InteractionManager.this.focused != null) InteractionManager.this.focused.onFocusLost();
                    InteractionManager.this.focused = hit;
                    if (InteractionManager.this.focused != null) InteractionManager.this.focused.onFocusGained();
                }
                
                InteractionManager.this.active = hit;
                if (InteractionManager.this.active != null) InteractionManager.this.active.onMousePressed(e.getX(), e.getY());
                InteractionManager.this.parent.repaint();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (InteractionManager.this.active != null) {
                    InteractionManager.this.active.onMouseReleased(e.getX(), e.getY());
                    InteractionManager.this.active = null;
                    InteractionManager.this.parent.repaint();
                }
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                final Component hit = InteractionManager.this.findComponentAt(e.getX(), e.getY());
                if (hit != InteractionManager.this.hovered) {
                    if (InteractionManager.this.hovered != null) InteractionManager.this.hovered.onMouseExit();
                    if (hit != null) hit.onMouseEnter();
                    InteractionManager.this.hovered = hit;
                }
                if (hit != null) hit.onMouseMoved(e.getX(), e.getY());
                InteractionManager.this.parent.repaint();
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (InteractionManager.this.active != null) {
                    InteractionManager.this.active.onMouseDragged(e.getX(), e.getY());
                    InteractionManager.this.parent.repaint();
                }
            }
        };
    }

    public KeyAdapter getKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (InteractionManager.this.focused != null) {
                    InteractionManager.this.focused.onKeyPressed(e);
                    InteractionManager.this.parent.repaint();
                }
            }
            @Override
            public void keyTyped(final KeyEvent e) {
                if (InteractionManager.this.focused != null) {
                    InteractionManager.this.focused.onKeyTyped(e);
                    InteractionManager.this.parent.repaint();
                }
            }
            @Override
            public void keyReleased(final KeyEvent e) {
                if (InteractionManager.this.focused != null) {
                    InteractionManager.this.focused.onKeyReleased(e);
                    InteractionManager.this.parent.repaint();
                }
            }
        };
    }

    private Component findComponentAt(final int x, final int y) {
        return this.findRecursive(this.container.lastChild, x, y);
    }

    private Component findRecursive(final Component startChild, final int x, final int y) {
        Component child = startChild;
        while (child != null) {
            if (child.isVisible() && child.isHitTestable()) {
                if (child.contains(x, y)) {
                    final Component subHit = this.findRecursive(child.lastChild, x, y);
                    if (subHit != null) return subHit;
                    return child;
                }
            }
            child = child.prevSibling;
        }
        return null;
    }
}
