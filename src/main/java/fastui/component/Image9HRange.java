package fastui.component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image9Range - An Image9Slice that supports rendering a partial range.
 * Useful for progress bars, sliders, and timeline spans.
 */
public class Image9HRange extends Image9Slice implements Rangeable {

    private float minRange = 0f;
    private float maxRange = 1f;

    public Image9HRange(int left, int right, int top, int bottom, BufferedImage... layers) {
        super(left, right, top, bottom, layers);
    }

    @Override
    public void setRange(final float min, final float max) {
        this.minRange = Math.max(0, Math.min(1, min));
        this.maxRange = Math.max(this.minRange, Math.min(1, max));
        this.repaint();
    }

    @Override
    public void onRender(Graphics2D g) {
        if (this.needsRebake || this.cache == null) {
            this.bake();
        }

        if (this.cache != null) {
            final float targetX = this.getAbsoluteX() + this.minRange * this.width;
            final float targetW = (this.maxRange - this.minRange) * this.width;

            if (targetW > 0) {
                // Draw sub-section of the baked cache
                float srcX1 = this.minRange * this.width;
                float srcX2 = this.maxRange * this.width;

                g.drawImage(cache,
                        (int) targetX, (int) this.getAbsoluteY(), (int) (targetX + targetW), (int) (this.getAbsoluteY() + this.height),
                        (int) srcX1, 0, (int) srcX2, (int) this.height,
                        null);
            }
        }
    }
}
