package fastui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class Factory {

    /**
     * Creates a tiny, 3-sliceable rounded image.
     * Width will be exactly (arc * 2) + 1.
     */
    public static BufferedImage createSliceableLayer(final int height, final int arc, final Color color) {
        final int width = (arc * 2) + 1;
        final RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, width, height, arc, arc);
        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fill(rect);
        g2.dispose();
        return img;
    }

    public static BufferedImage createLabel(final String text, final Font font, final Color color) {
        // Measure text first
        final BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gTmp = tmp.createGraphics();
        gTmp.setFont(font);
        final FontMetrics fm = gTmp.getFontMetrics();
        final int w = fm.stringWidth(text);
        final int h = fm.getHeight();
        gTmp.dispose();

        // Bake onto exact-size image
        final BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(font);
        g2.setColor(color);
        g2.drawString(text, 0, fm.getAscent());
        g2.dispose();
        return img;
    }
}
