package fastui.component;

import fastui.Container;
import fastui.behaviour.Behaviour;
import fastui.layout.Layout;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected Container root;
    protected Component parent;
    
    // Cache-friendly linked sibling list (replacing ArrayList)
    public Component firstChild;
    public Component lastChild;
    public Component nextSibling;
    public Component prevSibling;

    // Bounds
    protected float x;
    protected float y;
    protected float width;
    protected float height;

    // Preferred, Min, Max
    public float preferredWidth;
    public float preferredHeight;
    public float minWidth;
    public float minHeight;
    public float maxWidth;
    public float maxHeight;

    // Weight for flex layouts
    public float weight;

    // Margins (No object allocation)
    public float marginTop;
    public float marginLeft;
    public float marginBottom;
    public float marginRight;

    // Padding (No object allocation)
    public float paddingTop;
    public float paddingLeft;
    public float paddingBottom;
    public float paddingRight;

    // Layout configuration (Stored on parent)
    public Layout layout;
    public float layoutA;
    public float layoutB;
    public int layoutFlags;
    public boolean layoutDirty = true;

    protected final List<Behaviour> behaviors = new ArrayList<>();
    protected boolean hitTestable = true;
    protected boolean visible = true;

    public void setLayout(final Layout layout) {
        this.layout = layout;
        this.doLayout();
    }

    public Layout getLayout() {
        return this.layout;
    }

    public void doLayout() {
        if (this.layout != null) {
            this.layout.apply(this);
        }
    }

    public void add(final Component child) {
        if (child == null) return;
        if (child.parent != null) {
            child.parent.remove(child);
        }
        child.parent = this;
        child.root = this.root;
        
        if (this.firstChild == null) {
            this.firstChild = child;
            this.lastChild = child;
        } else {
            this.lastChild.nextSibling = child;
            child.prevSibling = this.lastChild;
            this.lastChild = child;
        }
        
        child.setRoot(this.root);
        this.doLayout();
    }

    public void remove(final Component child) {
        if (child == null || child.parent != this) return;
        
        if (child.prevSibling != null) {
            child.prevSibling.nextSibling = child.nextSibling;
        } else {
            this.firstChild = child.nextSibling;
        }
        
        if (child.nextSibling != null) {
            child.nextSibling.prevSibling = child.prevSibling;
        } else {
            this.lastChild = child.prevSibling;
        }
        
        child.parent = null;
        child.nextSibling = null;
        child.prevSibling = null;
        child.setRoot(null);
        this.doLayout();
    }

    public void clear() {
        Component child = this.firstChild;
        while (child != null) {
            Component next = child.nextSibling;
            child.parent = null;
            child.nextSibling = null;
            child.prevSibling = null;
            child.setRoot(null);
            child = next;
        }
        this.firstChild = null;
        this.lastChild = null;
        this.doLayout();
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
        
        Component child = this.firstChild;
        while (child != null) {
            child.render(g);
            child = child.nextSibling;
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

    public void setRoot(final Container root) {
        this.root = root;
        Component child = this.firstChild;
        while (child != null) {
            child.setRoot(root);
            child = child.nextSibling;
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

    public void setPadding(final float padding) {
        this.paddingTop = padding;
        this.paddingLeft = padding;
        this.paddingBottom = padding;
        this.paddingRight = padding;
    }

    public void setPadding(final float top, final float left, final float bottom, final float right) {
        this.paddingTop = top;
        this.paddingLeft = left;
        this.paddingBottom = bottom;
        this.paddingRight = right;
    }

    public float getPaddingTop() { return paddingTop; }
    public float getPaddingLeft() { return paddingLeft; }
    public float getPaddingBottom() { return paddingBottom; }
    public float getPaddingRight() { return paddingRight; }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(final boolean visible) {
        this.visible = visible;
        this.repaint();
    }

    public Container getRoot() {
        return this.root;
    }

    public Component getParent() {
        return this.parent;
    }
}
