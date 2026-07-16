package fastui.component;

import java.awt.*;
import fastui.composable.Composable;

/**
 * A container component that restricts the rendering of all its children
 * to its own bounding box (x, y, width, height).
 */
public class ClipContainer extends Component {

    public void add(final Composable composable) {
        for (final Component c : composable.components()) {
            this.add(c);
        }
    }

    @Override
    public void onRender(Graphics2D g) {
        // The container itself has no visual representation
    }

    @Override
    public void render(Graphics2D g) {
        // 1. Save the previous clipping area
        Shape oldClip = g.getClip();

        // 2. Restrict drawing to this component's bounds
        g.clipRect((int) getAbsoluteX(), (int) getAbsoluteY(), (int) getWidth(), (int) getHeight());

        // 3. Render self (onRender) and ALL children
        super.render(g);

        // 4. Restore the previous clipping area
        g.setClip(oldClip);
    }
}
