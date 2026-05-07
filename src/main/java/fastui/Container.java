package fastui;

import fastui.component.Component;
import fastui.composable.Composable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Container extends JPanel {
    private final List<Component> children = new ArrayList<>();
    private final InteractionManager interactionManager;
    private BufferedImage buffer;

    public Container() {
        setLayout(null);
        setOpaque(true);
        setFocusable(true);
        setBackground(new Color(15, 15, 15));

        this.interactionManager = new InteractionManager(this, this.children);
        
        final MouseAdapter mouse = this.interactionManager.getMouseAdapter();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        addKeyListener(this.interactionManager.getKeyAdapter());
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        
        if (this.buffer == null || this.buffer.getWidth() != getWidth() || this.buffer.getHeight() != getHeight()) {
            this.buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        
        final Graphics2D g2d = this.buffer.createGraphics();
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        for (final Component child : this.children) {
            child.render(g2d);
        }
        
        g2d.dispose();
        g.drawImage(this.buffer, 0, 0, null);
    }

    public void add(final Component component) {
        component.setParent(this);
        this.children.add(component);
        repaint();
    }

    public void add(final Composable composable) {
        for (final Component c : composable.components()) {
            this.add(c);
        }
    }
}
