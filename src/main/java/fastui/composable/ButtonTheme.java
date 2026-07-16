package fastui.composable;

import java.awt.Color;

/**
 * ButtonTheme - Defines the visual style for a Button component.
 * Encapsulates colors, rounding, and optional borders for all states.
 */
public class ButtonTheme {
    
    public final Color base;
    public final Color hover;
    public final Color pressed;
    public final int arc;
    public final int borderWidth;
    public final Color borderColor;

    public ButtonTheme(Color base, Color hover, Color pressed, int arc) {
        this(base, hover, pressed, arc, 0, null);
    }

    public ButtonTheme(Color base, Color hover, Color pressed, int arc, int borderWidth, Color borderColor) {
        this.base = base;
        this.hover = hover;
        this.pressed = pressed;
        this.arc = arc;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;
    }

    // --- Predefined Themes (Explicitly defined) ---
    
    public static final ButtonTheme NEON_GREEN = new ButtonTheme(
        new Color(32, 255, 128),      // Base
        new Color(50, 255, 150),      // Hover
        new Color(20, 200, 100),      // Pressed
        12                            // Arc
    );

    public static final ButtonTheme DARK_GREY = new ButtonTheme(
        new Color(45, 45, 45),
        new Color(55, 55, 55),
        new Color(35, 35, 35),
        8
    );

    public static final ButtonTheme TRANSPARENT = new ButtonTheme(
        new Color(0, 0, 0, 0),
        new Color(255, 255, 255, 20),
        new Color(255, 255, 255, 40),
        0
    );
}
