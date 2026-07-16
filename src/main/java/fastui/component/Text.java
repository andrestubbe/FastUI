package fastui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class Text extends Image {

    private static final int PADDING = 12;
    private static final int CURSOR_BLINK_MS = 500;

    private final Font font;
    private final Color textColor;
    private final Color cursorColor;
    private final Color selectionColor;
    private StringBuilder textContent = new StringBuilder();
    private final Timer cursorTimer;
    private boolean focused = false;
    private boolean cursorVisible = false;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private Consumer<String> changeListener;

    public void addChangeListener(Consumer<String> listener) {
        this.changeListener = listener;
    }

    public Text(final Font font, final Color textColor, final Color cursorColor) {
        super(null);
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
    public void onMousePressed(final float mx, final float my) {
        final int index = this.getCharIndexAt(mx);
        this.selectionStart = index;
        this.selectionEnd = index;
        this.repaint();
    }

    @Override
    public void onMouseDragged(final float mx, final float my) {
        this.selectionEnd = this.getCharIndexAt(mx);
        this.repaint();
    }

    private int getCharIndexAt(final float mx) {
        final float localX = mx - this.x - PADDING;
        if (localX <= 0) return 0;

        final String str = this.textContent.toString();
        final BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gTmp = tmp.createGraphics();
        gTmp.setFont(this.font);
        final FontMetrics fm = gTmp.getFontMetrics();

        int bestIndex = 0;
        float minDiff = localX;

        for (int i = 0; i <= str.length(); i++) {
            float w = fm.stringWidth(str.substring(0, i));
            float diff = Math.abs(localX - w);
            if (diff < minDiff) {
                minDiff = diff;
                bestIndex = i;
            }
        }
        gTmp.dispose();
        return bestIndex;
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
        if (this.changeListener != null) {
            this.changeListener.accept(this.textContent.toString());
        }
        this.repaint();
    }

    private boolean hasSelection() {
        return this.selectionStart >= 0 && this.selectionStart != this.selectionEnd;
    }

    private void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    private void deleteSelection() {
        final int start = Math.min(this.selectionStart, this.selectionEnd);
        final int end = Math.max(this.selectionStart, this.selectionEnd);
        this.textContent.delete(start, end);
        this.clearSelection();
    }

    private void copySelection() {
        if (!this.hasSelection()) return;
        final int start = Math.min(this.selectionStart, this.selectionEnd);
        final int end = Math.max(this.selectionStart, this.selectionEnd);
        final String selected = this.textContent.substring(start, end);
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

    private float getTextWidth(final int charIndex) {
        if (charIndex == 0 || this.textContent.length() == 0) return 0;
        final BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gTmp = tmp.createGraphics();
        gTmp.setFont(this.font);
        final float w = gTmp.getFontMetrics().stringWidth(this.textContent.substring(0, charIndex));
        gTmp.dispose();
        return w;
    }

    private float getCursorX() {
        return this.x + PADDING + this.getTextWidth(this.textContent.length());
    }

    @Override
    public void onRender(final Graphics2D g) {
        if (this.focused && this.hasSelection()) {
            final int start = Math.min(this.selectionStart, this.selectionEnd);
            final int end = Math.max(this.selectionStart, this.selectionEnd);
            final float selX1 = this.getAbsoluteX() + PADDING + this.getTextWidth(start);
            final float selX2 = this.getAbsoluteX() + PADDING + this.getTextWidth(end);
            g.setColor(this.selectionColor);
            g.fillRect((int) selX1, (int) (this.getAbsoluteY() + PADDING), (int) (selX2 - selX1), (int) (this.height - PADDING * 2));
        }

        final BufferedImage img = this.getImage();
        if (img != null) {
            final float textY = this.getAbsoluteY() + (this.height - img.getHeight()) / 2f;
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(this.getAbsoluteX() + PADDING, textY);
            g2.drawImage(img, 0, 0, null);
            g2.dispose();
        }

        if (this.focused && this.cursorVisible && !this.hasSelection()) {
            final float cursorX = this.getAbsoluteX() + PADDING + this.getTextWidth(this.textContent.length());
            g.setColor(this.cursorColor);
            g.drawLine((int) cursorX, (int) (this.getAbsoluteY() + PADDING), (int) cursorX, (int) (this.getAbsoluteY() + this.height - PADDING));
        }
    }

    public String getText() {
        return this.textContent.toString();
    }

    public void setText(final String value) {
        this.textContent = new StringBuilder(value);
        this.clearSelection();
        this.bakeText();
        this.repaint();
    }
}
