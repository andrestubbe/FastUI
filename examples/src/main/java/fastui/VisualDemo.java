package fastui;

import fasttheme.FastTheme;
import fastui.component.Image;
import fastui.component.Image3x3;
import fastui.component.TimelineAxis;
import fastui.composable.Button;
import fastui.composable.Range;
import fastui.composable.ScrollBar;
import fastui.composable.TextField;
import fastui.composable.Timeline;

import javax.swing.*;
import java.awt.*;

public class VisualDemo {

    // --- Window Config ---
    private static final int FRAME_WIDTH = 1440;
    private static final int FRAME_HEIGHT = 1080;
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
        SwingUtilities.invokeLater(() -> new VisualDemo().start());
    }

    public void start() {
        System.setProperty("sun.java2d.uiScale", "1.0");

        final long now = System.currentTimeMillis();
        final long oneYearAgo = now - 365L * 24 * 60 * 60 * 1000;
        
        // 1. Unified Timeline System
        final Timeline timeline = new Timeline(
            oneYearAgo, now,
            200, 20, COLOR_PANEL_BG, // Updated Total height to 200
            TIMELINE_RANGE_H, COLOR_SLIDER_TRACK, COLOR_SLIDER_SPAN,
            FONT_TIMELINE, COLOR_TIMELINE_TICK, COLOR_TIMELINE_LABEL
        );
        timeline.setBounds(TIMELINE_X, TIMELINE_Y, TIMELINE_WIDTH, TIMELINE_AXIS_H, TIMELINE_RANGE_H, GAP);

        // 2. Search UI
        final TextField searchField = new TextField(50, 12, new Color(25, 25, 25), FONT_FIELD, Color.WHITE, COLOR_NEON_GREEN);
        searchField.setBounds(300, 120, 400, 50);
        searchField.setText("Search files...");

        final Button searchButton = new Button(50, 12, COLOR_NEON_GREEN, "SEARCH", FONT_FIELD, Color.BLACK);
        searchButton.setBounds(100, 120, 180, 50);

        // 3. Assembly
        final Container container = new Container();
        container.setBackground(COLOR_BACKGROUND);
        container.add(timeline);
        container.add(searchButton);
        container.add(searchField);

        final JFrame frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(container);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.addNotify();

        final long hwnd = FastTheme.getWindowHandle(frame);
        if (hwnd != 0) {
            FastTheme.setTitleBarDarkMode(hwnd, true);
            FastTheme.setTitleBarColor(hwnd, 15, 15, 15);
            FastTheme.setTitleBarTextColor(hwnd, 220, 220, 220);
        }
        
        frame.setVisible(true);
    }
}
