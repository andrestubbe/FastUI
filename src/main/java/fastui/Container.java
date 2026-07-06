package fastui;

import fastui.component.Component;
import fastui.composable.Composable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

public class Container extends JPanel {

    public Component firstChild;
    public Component lastChild;
    private final InteractionManager interactionManager;
    private BufferedImage buffer;

    public Container() {
        this.setLayout(null);
        this.setOpaque(true);
        this.setFocusable(true);
        this.setBackground(new Color(15, 15, 15));

        this.interactionManager = new InteractionManager(this, this);

        final MouseAdapter mouse = this.interactionManager.getMouseAdapter();
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(this.interactionManager.getKeyAdapter());
    }

    public void add(final Component component) {
        if (component == null) return;
        if (component.getParent() != null) {
            component.getParent().remove(component);
        }
        component.setRoot(this);

        if (this.firstChild == null) {
            this.firstChild = component;
            this.lastChild = component;
        } else {
            this.lastChild.nextSibling = component;
            component.prevSibling = this.lastChild;
            this.lastChild = component;
        }
        this.repaint();
    }

    public void add(final Composable composable) {
        if (composable == null) return;
        for (final Component c : composable.components()) {
            this.add(c);
        }
    }

    public void remove(final Component component) {
        if (component == null) return;

        if (component.prevSibling != null) {
            component.prevSibling.nextSibling = component.nextSibling;
        } else {
            this.firstChild = component.nextSibling;
        }

        if (component.nextSibling != null) {
            component.nextSibling.prevSibling = component.prevSibling;
        } else {
            this.lastChild = component.prevSibling;
        }

        component.nextSibling = null;
        component.prevSibling = null;
        component.setRoot(null);
        this.repaint();
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;

        // Rendering Tuning for Sharpness
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Component child = this.firstChild;
        while (child != null) {
            child.render(g2d);
            child = child.nextSibling;
        }
    }
}
