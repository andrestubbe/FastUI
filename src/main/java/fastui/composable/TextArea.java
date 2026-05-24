package fastui.composable;

import fastui.Factory;
import fastui.component.Component;
import fastui.component.Image9Slice;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TextArea implements Composable {
    private final Image9Slice background;
    private final TextDisplay display;
    
    public TextArea(float x, float y, float w, float h, int arc, Color bgColor, Font font, Color textColor) {
        this.background = new Image9Slice(arc, arc, arc, arc, Factory.createSliceableLayer((int)h, arc, bgColor));
        this.display = new TextDisplay(font, textColor);
        this.setBounds(x, y, w, h);
    }

    public TextArea(int arc, Color bgColor, Font font, Color textColor) {
        this(0, 0, 0, 0, arc, bgColor, font, textColor);
    }

    public void setBounds(float x, float y, float w, float h) {
        this.background.setBounds(x, y, w, h);
        this.display.setBounds(x + 15, y + 15, w - 30, h - 30);
    }

    public void setText(String text) {
        this.display.setText(text);
    }

    public void append(String text) {
        this.display.append(text);
    }

    @Override
    public Component[] components() {
        return new Component[]{background, display};
    }

    private static class TextDisplay extends Component {

        private final Font font;
        private final Color textColor;
        private final List<String> lines = new ArrayList<>();

        public TextDisplay(final Font font, final Color textColor) {
            this.font = font;
            this.textColor = textColor;
        }

        public void setText(final String text) {
            this.lines.clear();
            if (text != null) {
                for (final String s : text.split("\n")) this.lines.add(s);
            }
            this.repaint();
        }

        public void append(final String text) {
            if (text != null) {
                for (final String s : text.split("\n")) this.lines.add(s);
            }
            this.repaint();
        }

        @Override
        public void onRender(final Graphics2D g) {
            g.setFont(this.font);
            g.setColor(this.textColor);
            final FontMetrics fm = g.getFontMetrics();
            final float lineHeight = fm.getHeight();
            float curY = this.y + fm.getAscent();
            
            for (final String line : this.lines) {
                if (curY > this.y + this.height) break;
                g.drawString(line, this.x, curY);
                curY += lineHeight;
            }
        }
    }
}
