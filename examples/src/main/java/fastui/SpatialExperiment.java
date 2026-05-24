package fastui;

import fasttheme.FastTheme;
import fastui.composable.Button;
import fastui.composable.ButtonTheme;
import fastui.component.Component;
import fastui.Container;
import fastui.component.Image9Slice;
import fastui.component.Stage;
import fastui.component.Spatial;
import fastui.Factory;
import fastui.util.Animator;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SpatialExperiment {

    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeLater(() -> new SpatialExperiment().start());
    }

    public void start() {
        final JFrame frame = new JFrame("Spatial Experiment - Modular 3D Check");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);

        final Container container = new Container();
        container.setBackground(new Color(10, 10, 12));
        frame.setContentPane(container);

        // The 3D Stage
        final Stage stage = new Stage();
        stage.setBounds(0, 0, 1200, 800);
        container.add(stage);

        final Random rnd = new Random();
        final Font font = new Font("Segoe UI", Font.BOLD, 14);

        final java.util.List<Spatial> spatials = new java.util.ArrayList<>();
        final float[] yOffsets = new float[100];
        final float[] baseYs = new float[100];

        // Create 100 interactive 3D planes scattered in deep space
        for (int i = 0; i < 100; i++) {
            final int cardW = 150;
            final int cardH = 200;
            
            // Content for the plane
            final Color bgColor = new Color(15 + rnd.nextInt(35), 15 + rnd.nextInt(25), 40 + rnd.nextInt(90));
            final Image9Slice cardContent = new Image9Slice(15, 15, 15, 15, Factory.createSliceableLayer(cardH, 15, bgColor));
            
            final Button btn = new Button(15, cardH - 50, cardW - 30, 30, 8, new Color(220, 220, 220), "#" + i, font, Color.BLACK);
            for (Component c : btn.components()) {
                cardContent.add(c);
            }

            final Spatial spatial = new Spatial(cardContent);
            // Even wider spread for 100 cards
            float x = -400 + rnd.nextFloat() * 2000;
            float y = -200 + rnd.nextFloat() * 1000;
            float z = rnd.nextFloat() * 2000; // Deep Z spread
            
            spatial.setBounds(x, y, cardW, cardH);
            spatial.setZ(z);
            spatial.setMipmappingEnabled(true);
            
            stage.add(spatial);
            spatials.add(spatial);
            baseYs[i] = y;
            yOffsets[i] = rnd.nextFloat() * (float)Math.PI * 2;
        }

        final float[] targetRotation = {0f};
        final float[] currentRotation = {0f};
        final int[] frameCount = {0};
        final long[] lastTime = {System.currentTimeMillis()};
        final String[] fpsText = {"FPS: 0"};
        final boolean[] isPaused = {false};

        // Pause listener
        frame.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    isPaused[0] = !isPaused[0];
                }
            }
        });

        // Camera movement logic
        final Point lastMouse = new Point();
        container.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                lastMouse.setLocation(e.getPoint());
            }
        });
        
        container.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                int dx = e.getX() - lastMouse.x;
                int dy = e.getY() - lastMouse.y;
                
                if (SwingUtilities.isLeftMouseButton(e)) {
                    targetRotation[0] += dx * 0.15f;
                } else {
                    stage.setCameraZ(stage.getCameraZ() + dy * 2.0f);
                }
                lastMouse.setLocation(e.getPoint());
            }
        });

        container.addMouseWheelListener(e -> {
            stage.setCameraZ(stage.getTargetCameraZ() - e.getWheelRotation() * 150.0f);
        });

        frame.setVisible(true);

        // Set a dramatic focal length
        stage.setFocalLength(900); 

        // Simple animation loop
        new Animator(container, 60, () -> {
            stage.updateSmoothing();
            
            // FPS Counter
            frameCount[0]++;
            long now = System.currentTimeMillis();
            if (now - lastTime[0] > 1000) {
                fpsText[0] = "FPS: " + frameCount[0];
                frameCount[0] = 0;
                lastTime[0] = now;
                // Update Window Title
                frame.setTitle("FastUI 3D Experiment - 100 Planes - " + fpsText[0] + (isPaused[0] ? " [PAUSED]" : ""));
            }

            if (!isPaused[0]) {
                // Smoothly interpolate rotation
                currentRotation[0] += (targetRotation[0] - currentRotation[0]) * 0.04f;
                
                float time = System.currentTimeMillis() / 1000.0f;
                for (int i = 0; i < spatials.size(); i++) {
                    Spatial s = spatials.get(i);
                    s.setWorldRotationY(currentRotation[0]);
                    
                    // Add floating effect anchored to baseY
                    float floatY = (float)Math.sin(time * 1.1f + yOffsets[i]) * 25.0f;
                    s.setBounds(s.getX(), baseYs[i] + floatY, s.getWidth(), s.getHeight());
                }
            }
        }).start();
    }
}
