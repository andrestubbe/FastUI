package fastui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image9Slice extends Component implements ImageSwappable {

    protected BufferedImage cache;
    private BufferedImage[] layers;
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;
    protected boolean needsRebake = true;

    public Image9Slice(final int left, final int right, final int top, final int bottom, final BufferedImage... layers) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        this.layers = layers;
    }

    @Override
    public void setImages(final BufferedImage... layers) {
        this.layers = layers;
        this.needsRebake = true;
        this.repaint();
    }

    @Override
    public void setBounds(final float x, final float y, final float width, final float height) {
        if (this.width != width || this.height != height) {
            this.needsRebake = true;
        }
        super.setBounds(x, y, width, height);
    }

    @Override
    public void onRender(final Graphics2D g) {
        if (this.needsRebake || this.cache == null) {
            this.bake();
        }

        if (this.cache != null && g != null) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(this.getAbsoluteX(), this.getAbsoluteY());
            g2.drawImage(this.cache, 0, 0, null);
            g2.dispose();
        }
    }

    protected void bake() {
        if (this.width <= 0 || this.height <= 0) return;

        this.cache = new BufferedImage((int) this.width, (int) this.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = this.cache.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (this.layers != null) {
            for (final BufferedImage img : this.layers) {
                if (img == null) continue;

                final int iw = img.getWidth();
                final int ih = img.getHeight();

                final int sx1 = 0;
                final int sx2 = this.left;
                final int sx3 = iw - this.right;
                final int sx4 = iw;

                final int sy1 = 0;
                final int sy2 = this.top;
                final int sy3 = ih - this.bottom;
                final int sy4 = ih;

                final int dx1 = 0;
                final int dx2 = this.left;
                final int dx3 = (int) this.width - this.right;
                final int dx4 = (int) this.width;

                final int dy1 = 0;
                final int dy2 = this.top;
                final int dy3 = (int) this.height - this.bottom;
                final int dy4 = (int) this.height;

                g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
                g.drawImage(img, dx2, dy1, dx3, dy2, sx2, sy1, sx3, sy2, null);
                g.drawImage(img, dx3, dy1, dx4, dy2, sx3, sy1, sx4, sy2, null);

                g.drawImage(img, dx1, dy2, dx2, dy3, sx1, sy2, sx2, sy3, null);
                g.drawImage(img, dx2, dy2, dx3, dy3, sx2, sy2, sx3, sy3, null);
                g.drawImage(img, dx3, dy2, dx4, dy3, sx3, sy2, sx4, sy3, null);

                g.drawImage(img, dx1, dy3, dx2, dy4, sx1, sy3, sx2, sy4, null);
                g.drawImage(img, dx2, dy3, dx3, dy4, sx2, sy3, sx3, sy4, null);
                g.drawImage(img, dx3, dy3, dx4, dy4, sx3, sy3, sx4, sy4, null);
            }
        }
        g.dispose();
        this.needsRebake = false;
    }
}
