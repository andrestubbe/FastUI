package fastui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.StringSelection;

public class Text extends Image {
    private static final int PADDING = 12;
    private static final int CURSOR_BLINK_MS = 500;

    private final Font font;
    private final Color textColor;
    private final Color cursorColor;
    private final Color selectionColor;

    private StringBuilder textContent = new StringBuilder();
    private boolean focused = false;
    private boolean cursorVisible = false;
    private Timer cursorTimer;

    private int selectionStart = -1;
    private int selectionEnd = -1;

    public Text(final Font font, final Color textColor, final Color cursorColor) {
        super(null); // Initialize Image with null, will be baked later
        this.font = font;
        this.textColor = textColor;
        this.cursorColor = cursorColor;
        this.selectionColor = new Color(cursorColor.getRed(), cursorColor.getGreen(), cursorColor.getBlue(), 60);
        this.cursorTimer = new Timer(CURSOR_BLINK_MS, e -> {
            this.cursorVisible = !this.cursorVisible;
            this.repaint();
        });
    }

    @Override
    public void onFocusGained() {
        this.focused = true;
        this.cursorVisible = true;
        this.cursorTimer.start();
        this.repaint();
    }

    @Override
    public void onFocusLost() {
        this.focused = false;
        this.cursorVisible = false;
        this.cursorTimer.stop();
        this.clearSelection();
        this.repaint();
    }

    @Override
    public void onKeyPressed(final KeyEvent e) {
        if (e.isControlDown()) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                    this.selectionStart = 0;
                    this.selectionEnd = this.textContent.length();
                    this.repaint();
                    break;
                case KeyEvent.VK_C:
                    this.copySelection();
                    break;
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            this.clearSelection();
            this.repaint();
        }
    }

    @Override
    public void onKeyTyped(final KeyEvent e) {
        final char c = e.getKeyChar();
        if (c == KeyEvent.CHAR_UNDEFINED || e.isControlDown()) return;
        if (c == '\b') {
            if (this.hasSelection()) {
                this.deleteSelection();
            } else if (this.textContent.length() > 0) {
                this.textContent.deleteCharAt(this.textContent.length() - 1);
            }
        } else {
            if (this.hasSelection()) this.deleteSelection();
            this.textContent.append(c);
        }
        this.bakeText();
        this.repaint();
    }

    private boolean hasSelection() {
        return this.selectionStart >= 0 && this.selectionEnd > this.selectionStart;
    }

    private void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    private void deleteSelection() {
        this.textContent.delete(this.selectionStart, this.selectionEnd);
        this.clearSelection();
    }

    private void copySelection() {
        if (!this.hasSelection()) return;
        final String selected = this.textContent.substring(this.selectionStart, this.selectionEnd);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selected), null);
    }

    private void bakeText() {
        if (this.textContent.length() == 0) {
            this.setImage(null);
            return;
        }
        final String str = this.textContent.toString();
        final BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gTmp = tmp.createGraphics();
        gTmp.setFont(this.font);
        final FontMetrics fm = gTmp.getFontMetrics();
        final int w = fm.stringWidth(str);
        final int h = fm.getHeight();
        gTmp.dispose();

        final BufferedImage bakedText = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = bakedText.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(this.font);
        g2.setColor(this.textColor);
        g2.drawString(str, 0, fm.getAscent());
        g2.dispose();

        this.setImage(bakedText);
    }

    private int getTextWidth(final int charIndex) {
        if (charIndex == 0 || this.textContent.length() == 0) return 0;
        final BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gTmp = tmp.createGraphics();
        gTmp.setFont(this.font);
        final int w = gTmp.getFontMetrics().stringWidth(this.textContent.substring(0, charIndex));
        gTmp.dispose();
        return w;
    }

    private int getCursorX() {
        return this.x + PADDING + this.getTextWidth(this.textContent.length());
    }

    @Override
    public void render(final Graphics2D g) {
        // Draw selection highlight first
        if (this.focused && this.hasSelection()) {
            final int selX1 = this.x + PADDING + this.getTextWidth(this.selectionStart);
            final int selX2 = this.x + PADDING + this.getTextWidth(this.selectionEnd);
            g.setColor(this.selectionColor);
            g.fillRect(selX1, this.y + PADDING, selX2 - selX1, this.height - PADDING * 2);
        }

        // Draw the baked text image via Image base class
        // We calculate centered Y position for the image
        final BufferedImage img = this.getImage();
        if (img != null) {
            final int textY = this.y + (this.height - img.getHeight()) / 2;
            g.drawImage(img, this.x + PADDING, textY, null);
        }

        // Draw cursor
        if (this.focused && this.cursorVisible && !this.hasSelection()) {
            final int cursorX = this.getCursorX();
            g.setColor(this.cursorColor);
            g.drawLine(cursorX, this.y + PADDING, cursorX, this.y + this.height - PADDING);
        }
    }

    public String getText() { return this.textContent.toString(); }

    public void setText(final String value) {
        this.textContent = new StringBuilder(value);
        this.clearSelection();
        this.bakeText();
        this.repaint();
    }
}
