package fastui.util;

import java.awt.Graphics2D;
import java.awt.Shape;

public final class Clipping {

    private Clipping() {}

    public static Shape push(Graphics2D g2) {
        return g2.getClip();
    }

    public static void pop(Graphics2D g2, Shape oldClip) {
        g2.setClip(oldClip);
    }

    public static void clip(Graphics2D g2, float x, float y, float width, float height) {
        g2.clipRect((int)x, (int)y, (int)width, (int)height);
    }
}
