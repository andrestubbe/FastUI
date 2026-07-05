package fastui.component;

import fastui.Container;
import fastui.behaviour.Behaviour;
import fastui.layout.LayoutManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected Container root;
    protected Component parent;
    protected List<Component> children;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected final List<Behaviour> behaviors = new ArrayList<>();
    protected boolean hitTestable = true;
    protected boolean visible = true;
    protected LayoutManager layoutManager;
    protected float marginTop;
    protected float marginLeft;
    protected float marginBottom;
    protected float marginRight;

    public void setLayout(final LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        this.doLayout();
    }

    public LayoutManager getLayout() {
        return this.layoutManager;
    }

    public void doLayout() {
        if (this.layoutManager != null && this.children != null) {
            this.layoutManager.layout(this, this.children);
        }
    }

    public void add(final Component child) {
        if (this.children == null) {
            this.children = new ArrayList<>(2);
        }
        child.parent = this;
        child.root = this.root;
        this.children.add(child);
    }

    public void addBehavior(final Behaviour behavior) {
        this.behaviors.add(behavior);
    }

    public void onMousePressed(final float mx, final float my) {
        for (final Behaviour b : this.behaviors) {
            b.onMousePressed(this, mx, my);
        }
    }

    public void onMouseReleased(final float mx, final float my) {
        for (final Behaviour b : this.behaviors) {
            b.onMouseReleased(this, mx, my);
        }
    }

    public void onMouseMoved(final float mx, final float my) {
        for (final Behaviour b : this.behaviors) {
            b.onMouseMoved(this, mx, my);
        }
    }

    public void onMouseDragged(final float mx, final float my) {
        for (final Behaviour b : this.behaviors) {
            b.onMouseDragged(this, mx, my);
        }
    }

    public void onMouseEnter() {
        for (final Behaviour b : this.behaviors) {
            b.onMouseEnter(this);
        }
    }

    public void onMouseExit() {
        for (final Behaviour b : this.behaviors) {
            b.onMouseExit(this);
        }
    }

    public void onFocusGained() {
        for (final Behaviour b : this.behaviors) {
            b.onFocusGained(this);
        }
    }

    public void onFocusLost() {
        for (final Behaviour b : this.behaviors) {
            b.onFocusLost(this);
        }
    }

    public void onKeyPressed(final KeyEvent e) {
        for (final Behaviour b : this.behaviors) {
            b.onKeyPressed(this, e);
        }
    }

    public void onKeyTyped(final KeyEvent e) {
        for (final Behaviour b : this.behaviors) {
            b.onKeyTyped(this, e);
        }
    }

    public void onKeyReleased(final KeyEvent e) {
        for (final Behaviour b : this.behaviors) {
            b.onKeyReleased(this, e);
        }
    }

    public void repaint() {
        if (this.root != null) {
            this.root.repaint();
        } else if (this.parent != null) {
            this.parent.repaint();
        }
    }

    public boolean contains(final float mx, final float my) {
        final float ax = this.getAbsoluteX();
        final float ay = this.getAbsoluteY();
        return mx >= ax &&
                mx <= ax + this.width &&
                my >= ay &&
                my <= ay + this.height;
    }

    public abstract void onRender(final Graphics2D g);

    public void render(final Graphics2D g) {
        if (!this.visible) return;
        this.onRender(g);
        for (final Behaviour b : this.behaviors) {
            b.onRender(this, g);
        }
        if (this.children != null) {
            for (final Component child : this.children) {
                child.render(g);
            }
        }
    }

    public float getAbsoluteX() {
        return (this.parent == null) ? this.x : this.parent.getAbsoluteX() + this.x;
    }

    public float getAbsoluteY() {
        return (this.parent == null) ? this.y : this.parent.getAbsoluteY() + this.y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isHitTestable() {
        return this.hitTestable;
    }

    public List<Component> getChildren() {
        return this.children;
    }

    public void setRoot(final Container root) {
        this.root = root;
        if (this.children != null) {
            for (final Component child : this.children) child.setRoot(root);
        }
    }

    public void setHitTestable(final boolean hitTestable) {
        this.hitTestable = hitTestable;
    }

    public void setBounds(final float x, final float y, final float width, final float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.doLayout();
    }

    public void setX(final float x) {
        this.x = x;
        this.doLayout();
    }

    public void setY(final float y) {
        this.y = y;
        this.doLayout();
    }

    public void setWidth(final float width) {
        this.width = width;
        this.doLayout();
    }

    public void setHeight(final float height) {
        this.height = height;
        this.doLayout();
    }

    public void setMargin(final float margin) {
        this.marginTop = margin;
        this.marginLeft = margin;
        this.marginBottom = margin;
        this.marginRight = margin;
    }

    public void setMargin(final float top, final float left, final float bottom, final float right) {
        this.marginTop = top;
        this.marginLeft = left;
        this.marginBottom = bottom;
        this.marginRight = right;
    }

    public float getMarginTop() { return marginTop; }
    public float getMarginLeft() { return marginLeft; }
    public float getMarginBottom() { return marginBottom; }
    public float getMarginRight() { return marginRight; }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
        this.repaint();
    }
}
