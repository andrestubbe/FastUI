package fastui;

import fastui.component.Component;
import fastui.component.Stage;
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
        this.setLayout(null);
        this.setOpaque(true);
        this.setFocusable(true);
        this.setBackground(new Color(15, 15, 15));

        this.interactionManager = new InteractionManager(this, this.children);
        
        final MouseAdapter mouse = this.interactionManager.getMouseAdapter();
        this.addMouseListener(mouse);
        this.addMouseMotionListener(mouse);
        this.addKeyListener(this.interactionManager.getKeyAdapter());
    }

    public void add(final Component component) {
        component.setRoot(this);
        this.children.add(component);
        this.repaint();
    }

    public void add(final Composable composable) {
        for (final Component c : composable.components()) {
            this.add(c);
        }
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

        for (final Component child : this.children) {
            child.render(g2d);
        }
    }
}
