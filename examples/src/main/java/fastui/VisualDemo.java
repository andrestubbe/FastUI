package fastui;

import fasttheme.FastTheme;
import fastui.composable.Button;
import fastui.composable.ButtonTheme;
import fastui.composable.TextField;
import fastui.composable.Timeline;
import fastui.component.Image;
import fastui.component.Image9Slice;
import fastui.component.Spatial;
import fastui.component.Stage;
import fastui.util.Animator;
import fastui.Container;
import fastui.Factory;

import fastui.composable.Button;
import fastui.behaviour.BehaviorButton3x3;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VisualDemo {

    // --- Window Config ---
    private static final int FRAME_WIDTH = 1440;
    private static final int FRAME_HEIGHT = 1200;
    private static final String FRAME_TITLE = "FastUI 2.0 - Unified Sequencer";
    private static final int GAP = 15; // Smaller gap for unified look

    // --- Design Tokens ---
    private static final Color COLOR_BACKGROUND = new Color(15, 15, 15);
    private static final Color COLOR_NEON_GREEN = new Color(32, 255, 128);

    private static final int TIMELINE_X = 100;
    private static final int TIMELINE_Y = 220;
    private static final int TIMELINE_WIDTH = 1240;
    private static final int TIMELINE_AXIS_H = 50;
    private static final int TIMELINE_RANGE_H = 90;

    private static final Color COLOR_PANEL_BG = new Color(22, 22, 22);
    private static final Color COLOR_SLIDER_TRACK = new Color(35, 35, 35);
    private static final Color COLOR_SLIDER_SPAN = new Color(32, 255, 128, 160); // Semi-transparent green for the range
    private static final Color COLOR_TIMELINE_TICK = new Color(60, 60, 60);
    private static final Color COLOR_TIMELINE_LABEL = new Color(140, 140, 140);

    private static final Font FONT_TIMELINE = new Font("Inter", Font.PLAIN, 22);
    private static final Font FONT_FIELD = new Font("Inter", Font.PLAIN, 22);

    public static void main(final String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> new VisualDemo().start());
    }

    public void start() {
        System.setProperty("sun.java2d.uiScale", "1.0");

        final long now = System.currentTimeMillis();
        final long oneYearAgo = now - 365L * 24 * 60 * 60 * 1000;

        // 1. Unified Timeline System
        final Timeline timeline = new Timeline(
                TIMELINE_X, TIMELINE_Y, TIMELINE_WIDTH, TIMELINE_AXIS_H, TIMELINE_RANGE_H, GAP,
                oneYearAgo, now,
                20, COLOR_PANEL_BG,
                COLOR_SLIDER_TRACK, COLOR_SLIDER_SPAN,
                FONT_TIMELINE, COLOR_TIMELINE_TICK, COLOR_TIMELINE_LABEL
        );

        // 2. Search UI
        final TextField searchField = new TextField(100, 120, 400, 50, 12, new Color(25, 25, 25), FONT_FIELD, Color.WHITE, COLOR_NEON_GREEN);
        searchField.setText("Search files...");

        final Button searchButton = new Button(515, 120, 150, 50, ButtonTheme.NEON_GREEN, "SEARCH", FONT_FIELD, Color.BLACK);

        // 3. Spatial Stage Demo (3D Section)
        final Stage stage = new Stage();
        stage.setBounds(TIMELINE_X, TIMELINE_Y + TIMELINE_AXIS_H + TIMELINE_RANGE_H + 80, TIMELINE_WIDTH, 600);

        java.awt.image.BufferedImage img1 = null;
        java.awt.image.BufferedImage img2 = null;
        try {
            img1 = ImageIO.read(new File("C:\\Users\\andre\\.gemini\\antigravity\\brain\\37637aa4-9211-4b96-a6f1-9ee5e3ba3bee\\file_thumbnail_1_1778575535034.png"));
            img2 = ImageIO.read(new File("C:\\Users\\andre\\.gemini\\antigravity\\brain\\37637aa4-9211-4b96-a6f1-9ee5e3ba3bee\\file_thumbnail_2_1778575552560.png"));
        } catch (Exception e) {}

        final java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 20; i++) {
            final int cardW = 180;
            final int cardH = 250;
            
            final Color cardColor = new Color(40 + rnd.nextInt(40), 40 + rnd.nextInt(40), 40 + rnd.nextInt(40));
            final Image9Slice card = new Image9Slice(15, 15, 15, 15, Factory.createSliceableLayer(cardH, 15, cardColor));
            
            if (img1 != null && i % 2 == 0) {
                final fastui.component.Image icon = new fastui.component.Image(img1);
                icon.setBounds(20, 20, cardW - 40, cardH - 80);
                card.add(icon);
            } else if (img2 != null) {
                final fastui.component.Image icon = new fastui.component.Image(img2);
                icon.setBounds(20, 20, cardW - 40, cardH - 80);
                card.add(icon);
            }

            final Button btn = new Button(20, cardH - 50, cardW - 40, 35, 8, new Color(60, 60, 60), "CLICK", FONT_FIELD.deriveFont(14f), Color.WHITE);
            for (final fastui.component.Component c : btn.components()) {
                card.add(c);
            }

            final Spatial spatial = new Spatial(card);
            spatial.setBounds(rnd.nextInt(TIMELINE_WIDTH - 200), rnd.nextInt(300), cardW, cardH);
            spatial.setZ(rnd.nextFloat() * 50.0f); 
            spatial.setMipmappingEnabled(true);
            
            stage.add(spatial);
        }

        // 4. Assembly
        final Container container = new Container();
        container.setBackground(COLOR_BACKGROUND);
        container.add(timeline);
        container.add(searchField);
        container.add(searchButton);
        container.add(stage);

        final JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(container);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        
        // Interaction: Camera Drag
        final Point lastMouse = new Point();
        final boolean[] isDraggingCamera = {false};

        container.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                lastMouse.setLocation(e.getPoint());
                isDraggingCamera[0] = stage.contains(e.getX(), e.getY());
            }
        });
        
        container.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                if (!isDraggingCamera[0]) return;

                int dx = e.getX() - lastMouse.x;
                int dy = e.getY() - lastMouse.y;
                
                if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    stage.setCameraX(stage.getCameraX() - dx);
                    stage.setCameraY(stage.getCameraY() - dy);
                } else {
                    stage.setCameraZ(stage.getCameraZ() + dy * 0.1f);
                }
                
                lastMouse.setLocation(e.getPoint());
            }
        });

        container.addMouseWheelListener(e -> {
            // Proportional zoom: smaller steps when close, larger when far
            float distance = stage.getTargetCameraZ() + stage.getFocalLength();
            float step = Math.max(0.1f, Math.abs(distance) * 0.1f);

            float wheel = e.getWheelRotation();
            float newZ = stage.getTargetCameraZ() - wheel * step;

            stage.setCameraZ(newZ);
        });

        frame.addNotify();

        final long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarDarkMode(hwnd, true);
            FastTheme.setTitleBarColor(hwnd, 15, 15, 15);
            FastTheme.setTitleBarTextColor(hwnd, 220, 220, 220);
        }

        final Animator animator = new Animator(container, 60, () -> {
            stage.updateSmoothing();
        });
        animator.start();

        frame.setVisible(true);
    }
}
