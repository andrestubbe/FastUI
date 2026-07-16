package fastui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image extends Component implements ImageSwappable {

    private BufferedImage image;

    public Image(final BufferedImage image) {
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
    }

    @Override
    public void onRender(final Graphics2D g) {
        if (this.image != null) {
            final Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(this.getAbsoluteX(), this.getAbsoluteY());
            g2.drawImage(this.image, 0, 0, (int) this.width, (int) this.height, null);
            g2.dispose();
        }
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(final BufferedImage image) {
        this.image = image;
        if (image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        }
        this.repaint();
    }

    @Override
    public void setImages(BufferedImage... layers) {
        if (layers != null && layers.length > 0) {
            this.image = layers[0];
            if (this.image != null) {
                this.width = this.image.getWidth();
                this.height = this.image.getHeight();
            }
            this.repaint();
        }
    }
}
