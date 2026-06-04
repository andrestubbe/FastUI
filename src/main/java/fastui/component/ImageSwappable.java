package fastui.component;

import java.awt.image.BufferedImage;

/**
 * Interface for components whose internal textures/images can be swapped at runtime
 * (e.g., for hover/pressed states or animations).
 */
public interface ImageSwappable {
    void setImages(BufferedImage... layers);
}
