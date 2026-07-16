package fastui.component;

import java.awt.Color;
import java.awt.Graphics2D;
import fastui.composable.Composable;

public class Panel extends Component {
    private Color backgroundColor;

    public Panel() {
        this.backgroundColor = null;
    }

    public Panel(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void add(final Composable composable) {
        for (final Component c : composable.components()) {
            this.add(c);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        if (backgroundColor != null) {
            g.setColor(backgroundColor);
            g.fillRect((int) getAbsoluteX(), (int) getAbsoluteY(), (int) getWidth(), (int) getHeight());
        }
    }
}
