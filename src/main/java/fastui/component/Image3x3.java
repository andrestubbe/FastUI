package fastui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image3x3 extends Component {

    private BufferedImage[] layers;
    private float minRange = 0f;
    private float maxRange = 1f;

    public Image3x3(final BufferedImage... layers) {
        this.layers = layers;
    }

    public void setRange(final float min, final float max) {
        this.minRange = Math.max(0, Math.min(1, min));
        this.maxRange = Math.max(this.minRange, Math.min(1, max));
        this.repaint();
    }

    public void setImages(final BufferedImage... layers) {
        this.layers = layers;
        this.repaint();
    }

    @Override
    public void render(final Graphics2D g) {
        if (this.layers == null || this.layers.length == 0) return;
        final int targetX = (int) (this.x + this.minRange * this.width);
        final int targetW = (int) ((this.maxRange - this.minRange) * this.width);
        if (targetW <= 0) return;
        for (final BufferedImage img : this.layers) {
            if (img == null) continue;
            final int iw = img.getWidth();
            final int ih = img.getHeight();
            final int arc = (iw - 1) / 2;
            g.drawImage(img, targetX, this.y, targetX + arc, this.y + this.height, 0, 0, arc, ih, null);
            g.drawImage(img, targetX + arc, this.y, targetX + targetW - arc, this.y + this.height, arc, 0, arc + 1, ih, null);
            g.drawImage(img, targetX + targetW - arc, this.y, targetX + targetW, this.y + this.height, iw - arc, 0, iw, ih, null);
        }
    }
}
