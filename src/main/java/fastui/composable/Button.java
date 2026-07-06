package fastui.composable;

import fastui.Factory;
import fastui.behaviour.BehaviorButton3x3;
import fastui.behaviour.Behaviour;
import fastui.component.Component;
import fastui.component.Image;
import fastui.component.Image9Slice;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button implements Composable {

    private final Image9Slice background;
    private final Image label;
    private float labelW;
    private float labelH;
    private boolean leftAligned = false;
    private float lastX, lastY, lastW, lastH;

    private String originalText;
    private Font font;
    private Color textColor;
    private String currentDisplayText;

    // --- Constructor for Text ---
    public Button(
            final float x, final float y,
            final float width, final float height,
            final ButtonTheme theme,
            final String text, final Font font, final Color textColor
    ) {
        this.originalText = text;
        this.font = font;
        this.textColor = textColor;
        this.currentDisplayText = text;

        final BufferedImage base = createBackground(height, theme, theme.base);
        final BufferedImage hover = createBackground(height, theme, theme.hover);
        final BufferedImage pressed = createBackground(height, theme, theme.pressed);

        int slice = theme.arc / 2;
        this.background = new Image9Slice(slice, slice, slice, slice, base);
        this.background.addBehavior(new BehaviorButton3x3(base, hover, pressed));

        final BufferedImage bakedLabel = Factory.createLabel(text, font, textColor);
        this.label = new Image(bakedLabel);
        this.label.setHitTestable(false);
        this.labelW = (float) bakedLabel.getWidth();
        this.labelH = (float) bakedLabel.getHeight();

        if (width > 0) {
            this.setBounds(x, y, width, height);
        }
    }

    // --- Constructor for Icons ---
    public Button(
            final float x, final float y,
            final float width, final float height,
            final ButtonTheme theme,
            final BufferedImage icon
    ) {
        final BufferedImage base = createBackground(height, theme, theme.base);
        final BufferedImage hover = createBackground(height, theme, theme.hover);
        final BufferedImage pressed = createBackground(height, theme, theme.pressed);

        int slice = theme.arc / 2;
        this.background = new Image9Slice(slice, slice, slice, slice, base);
        this.background.addBehavior(new BehaviorButton3x3(base, hover, pressed));

        this.label = new Image(icon);
        this.label.setHitTestable(false);
        this.labelW = (float) icon.getWidth();
        this.labelH = (float) icon.getHeight();

        if (width > 0) {
            this.setBounds(x, y, width, height);
        }
    }

    private static BufferedImage createBackground(float height, ButtonTheme theme, Color stateColor) {
        if (theme.borderWidth > 0 && theme.borderColor != null) {
            return Factory.createSliceableLayer((int) height, theme.arc, theme.borderWidth, stateColor, theme.borderColor);
        } else {
            return Factory.createSliceableLayer((int) height, theme.arc, stateColor);
        }
    }

    public void setLeftAligned(boolean leftAligned) {
        this.leftAligned = leftAligned;
        if (lastW > 0) {
            this.setBounds(lastX, lastY, lastW, lastH);
        }
    }

    public void setBounds(final float x, final float y, final float width, final float height) {
        this.lastX = x;
        this.lastY = y;
        this.lastW = width;
        this.lastH = height;
        this.background.setBounds(x, y, width, height);

        if (originalText != null && font != null && textColor != null) {
            float padding = leftAligned ? 24 : 16;
            float availableW = width - padding;
            
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
            int textW = fm.stringWidth(originalText);
            
            String displayText = originalText;
            if (textW > availableW) {
                String ellipsis = "...";
                int ellipsisW = fm.stringWidth(ellipsis);
                if (availableW > ellipsisW) {
                    int len = originalText.length();
                    while (len > 0 && fm.stringWidth(originalText.substring(0, len)) + ellipsisW > availableW) {
                        len--;
                    }
                    displayText = originalText.substring(0, len) + ellipsis;
                } else {
                    displayText = "";
                }
            }
            
            if (!displayText.equals(currentDisplayText)) {
                currentDisplayText = displayText;
                BufferedImage bakedLabel = Factory.createLabel(displayText, font, textColor);
                this.label.setImage(bakedLabel);
                this.labelW = (float) bakedLabel.getWidth();
                this.labelH = (float) bakedLabel.getHeight();
            }
        }

        final float lx = leftAligned ? x + 12 : x + (width - this.labelW) / 2f;
        final float ly = y + (height - this.labelH) / 2f;
        this.label.setBounds(lx, ly, this.labelW, this.labelH);
    }

    public void addBehavior(Behaviour behavior) {
        this.background.addBehavior(behavior);
    }

    @Override
    public Component[] components() {
        return new Component[]{this.background, this.label};
    }
}
