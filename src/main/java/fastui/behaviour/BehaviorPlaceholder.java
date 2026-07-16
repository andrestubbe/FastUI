package fastui.behaviour;

import fastui.component.Component;
import fastui.component.TextDisplay;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class BehaviorPlaceholder implements Behaviour {
    private final String text;
    private final Color color;

    public BehaviorPlaceholder(final String text, final Color color) {
        this.text = text != null ? text : "";
        this.color = color != null ? color : Color.GRAY;
    }

    @Override
    public void onRender(final Component target, final Graphics2D g) {
        if (target instanceof TextDisplay) {
            final TextDisplay display = (TextDisplay) target;
            if (display.getText().isEmpty()) {
                g.setFont(display.getFont());
                g.setColor(this.color);
                
                final FontMetrics fm = g.getFontMetrics();
                final float absX = display.getAbsoluteX();
                final float absY = display.getAbsoluteY();
                final float curY = absY + fm.getAscent();
                
                g.drawString(this.text, absX, curY);
            }
        }
    }
}
