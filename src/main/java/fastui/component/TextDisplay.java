package fastui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextDisplay extends Component {

    private final Font font;
    private final Color textColor;
    private final StringBuilder content = new StringBuilder();
    private final List<String> lines = new ArrayList<>();

    private boolean focused = false;

    private boolean cursorVisible = false;
    private final Timer cursorTimer;
    private Consumer<String> changeListener;
    private int selectionStart = -1;
    private int selectionEnd = -1;

    public boolean hasSelection() {
        return this.selectionStart >= 0 && this.selectionStart != this.selectionEnd;
    }

    public void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    public void deleteSelection() {
        this.content.setLength(0);
        this.clearSelection();
        this.notifyChange();
    }

    public void deleteLastChar() {
        if (content.length() > 0) {
            content.deleteCharAt(content.length() - 1);
            this.notifyChange();
        }
    }

    public void insertChar(char c) {
        content.append(c);
        this.notifyChange();
    }

    public void insertNewLine() {
        content.append('\n');
        this.notifyChange();
    }

    public void selectAll() {
        this.selectionStart = 0;
        this.selectionEnd = content.length();
        this.repaint();
    }

    public TextDisplay(final Font font, final Color textColor) {
        this.font = font;
        this.textColor = textColor;
        this.cursorTimer = new Timer(500, e -> {
            this.cursorVisible = !this.cursorVisible;
            this.repaint();
        });
    }

    public void addChangeListener(Consumer<String> listener) {
        this.changeListener = listener;
    }

    @Override
    public void onFocusGained() {
        super.onFocusGained();
        this.focused = true;
        this.cursorVisible = true;
        this.cursorTimer.start();
        this.repaint();
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        this.focused = false;
        this.cursorVisible = false;
        this.cursorTimer.stop();
        this.clearSelection();
        this.repaint();
    }

    public void notifyChange() {
        if (this.changeListener != null) {
            this.changeListener.accept(this.content.toString());
        }
        this.repaint();
    }

    public void append(final String text) {
        if (text != null) {
            this.content.append(text);
        }
        this.notifyChange();
    }

    private void wrapText(FontMetrics fm, float maxWidth) {
        this.lines.clear();
        String rawText = this.content.toString();
        if (rawText.isEmpty()) {
            return;
        }
        if (maxWidth <= 20) {
            this.lines.add(rawText);
            return;
        }

        String[] paragraphs = rawText.split("\n", -1);
        for (String paragraph : paragraphs) {
            if (paragraph.isEmpty()) {
                this.lines.add("");
                continue;
            }

            int start = 0;
            while (start < paragraph.length()) {
                int end = start;
                int lastSpace = -1;

                while (end < paragraph.length()) {
                    char c = paragraph.charAt(end);
                    if (c == ' ') {
                        lastSpace = end;
                    }

                    String candidate = paragraph.substring(start, end + 1);
                    if (fm.stringWidth(candidate) > maxWidth) {
                        break;
                    }
                    end++;
                }

                if (end == start) {
                    end = start + 1;
                } else if (end < paragraph.length() && lastSpace > start) {
                    end = lastSpace;
                }

                this.lines.add(paragraph.substring(start, end));

                start = end;
                if (start < paragraph.length() && paragraph.charAt(start) == ' ') {
                    start++;
                }
            }
        }
    }

    @Override
    public void onRender(final Graphics2D g) {
        g.setFont(this.font);
        final FontMetrics fm = g.getFontMetrics();
        final float lineHeight = fm.getHeight();

        float absX = this.getAbsoluteX();
        float absY = this.getAbsoluteY();
        float curY = absY + fm.getAscent();

        wrapText(fm, this.width);



        g.setColor(this.textColor);

        if (this.focused && hasSelection() && !lines.isEmpty()) {
            g.setColor(new Color(0, 120, 215, 60));
            float totalH = lines.size() * lineHeight;
            g.fillRect((int) absX, (int) absY, (int) this.width, (int) Math.min(totalH, this.height));
        }

        if (lines.isEmpty()) {
            if (focused && cursorVisible) {
                g.fillRect((int) absX, (int) (curY - fm.getAscent()), 2, (int) lineHeight);
            }
        } else {
            for (int i = 0; i < this.lines.size(); i++) {
                String line = this.lines.get(i);
                if (curY > absY + this.height) break;
                g.drawString(line, absX, curY);

                if (i == this.lines.size() - 1 && focused && cursorVisible) {
                    float textW = fm.stringWidth(line);
                    g.fillRect((int) (absX + textW), (int) (curY - fm.getAscent()), 2, (int) lineHeight);
                }
                curY += lineHeight;
            }
        }
    }

    public Font getFont() {
        return this.font;
    }

    public float getPreferredHeight(float maxWidth) {
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmp.createGraphics();
        g.setFont(this.font);
        FontMetrics fm = g.getFontMetrics();
        wrapText(fm, maxWidth);
        float lineHeight = fm.getHeight();
        int linesCount = Math.max(1, this.lines.size());
        g.dispose();
        return linesCount * lineHeight;
    }

    public String getText() {
        return this.content.toString();
    }

    public void setText(final String text) {
        this.content.setLength(0);
        if (text != null) {
            this.content.append(text);
        }
        this.notifyChange();
    }
}
