package fastui;

import fastui.component.Component;
import java.awt.event.*;
import java.util.List;

public class InteractionManager {
    private final List<Component> children;
    private final java.awt.Component parent;

    private Component hovered = null;
    private Component active = null;
    private Component focused = null;

    public InteractionManager(final java.awt.Component parent, final List<Component> children) {
        this.parent = parent;
        this.children = children;
    }

    public MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                parent.requestFocusInWindow();
                final Component hit = findComponentAt(e.getX(), e.getY());
                
                if (hit != focused) {
                    if (focused != null) focused.onFocusLost();
                    focused = hit;
                    if (focused != null) focused.onFocusGained();
                }
                
                active = hit;
                if (active != null) active.onMousePressed(e.getX(), e.getY());
                parent.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (active != null) {
                    active.onMouseReleased(e.getX(), e.getY());
                    active = null;
                    parent.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                final Component hit = findComponentAt(e.getX(), e.getY());
                if (hit != hovered) {
                    if (hovered != null) hovered.onMouseExit();
                    if (hit != null) hit.onMouseEnter();
                    hovered = hit;
                }
                if (hit != null) hit.onMouseMoved(e.getX(), e.getY());
                parent.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (active != null) {
                    active.onMouseDragged(e.getX(), e.getY());
                    parent.repaint();
                }
            }
        };
    }

    public KeyAdapter getKeyAdapter() {
        return new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (focused != null) {
                    focused.onKeyPressed(e);
                    parent.repaint();
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {
                if (focused != null) {
                    focused.onKeyTyped(e);
                    parent.repaint();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (focused != null) {
                    focused.onKeyReleased(e);
                    parent.repaint();
                }
            }
        };
    }

    private Component findComponentAt(int x, int y) {
        for (int i = children.size() - 1; i >= 0; i--) {
            final Component child = children.get(i);
            if (child.isHitTestable() && child.contains(x, y)) return child;
        }
        return null;
    }
}
