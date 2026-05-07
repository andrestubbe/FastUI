package fastui.component;

import fastui.Container;
import fastui.behaviour.MouseBehavior;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {
    protected int x, y, width, height;
    protected Container parent;
    protected List<MouseBehavior> behaviors = new ArrayList<>();
    protected boolean hitTestable = true;

    public void setHitTestable(boolean hitTestable) { this.hitTestable = hitTestable; }
    public boolean isHitTestable() { return hitTestable; }

    public void setBounds(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setParent(final Container parent) {
        this.parent = parent;
    }

    public void addBehavior(final MouseBehavior behavior) {
        this.behaviors.add(behavior);
    }

    public void onMousePressed(final int mx, final int my) {
        for (final MouseBehavior b : this.behaviors) b.onMousePressed(this, mx, my);
    }

    public void onMouseReleased(final int mx, final int my) {
        for (final MouseBehavior b : this.behaviors) b.onMouseReleased(this, mx, my);
    }

    public void onMouseMoved(final int mx, final int my) {
        for (final MouseBehavior b : this.behaviors) b.onMouseMoved(this, mx, my);
    }

    public void onMouseDragged(final int mx, final int my) {
        for (final MouseBehavior b : this.behaviors) b.onMouseDragged(this, mx, my);
    }

    public void onMouseEnter() {
        for (final MouseBehavior b : this.behaviors) b.onMouseEnter(this);
    }

    public void onMouseExit() {
        for (final MouseBehavior b : this.behaviors) b.onMouseExit(this);
    }

    public void onFocusGained() {
        for (final MouseBehavior b : this.behaviors) b.onFocusGained(this);
    }

    public void onFocusLost() {
        for (final MouseBehavior b : this.behaviors) b.onFocusLost(this);
    }

    public void onKeyPressed(final KeyEvent e) {
        for (final MouseBehavior b : this.behaviors) b.onKeyPressed(this, e);
    }

    public void onKeyTyped(final KeyEvent e) {
        for (final MouseBehavior b : this.behaviors) b.onKeyTyped(this, e);
    }

    public void onKeyTyped(final java.awt.Component target, final KeyEvent e) {
        // Compatibility helper if needed
    }

    public void onKeyReleased(final KeyEvent e) {
        for (final MouseBehavior b : this.behaviors) b.onKeyReleased(this, e);
    }

    public void repaint() {
        if (this.parent != null) this.parent.repaint();
    }

    public boolean contains(final int mx, final int my) {
        return mx >= this.x && mx <= this.x + this.width && my >= this.y && my <= this.y + this.height;
    }

    public abstract void render(Graphics2D g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
