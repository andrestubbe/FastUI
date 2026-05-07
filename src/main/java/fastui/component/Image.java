package fastui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image extends Component {
    private BufferedImage image;

    public Image(final BufferedImage image) {
        this.image = image;
    }

    public void setImage(final BufferedImage image) {
        this.image = image;
        this.repaint();
    }

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public void render(final Graphics2D g) {
        if (this.image != null) {
            g.drawImage(this.image, this.x, this.y, this.width, this.height, null);
        }
    }
}
