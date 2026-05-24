package fastui.component;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Stage extends Component {

    private float cameraX = 0f;
    private float cameraY = 0f;
    private float cameraZ = 0f;
    private float targetCameraZ = 0f;
    private float focalLength = 15.0f;

    public Stage() {
        this.setHitTestable(true);
    }

    public void updateSmoothing() {
        // Smoothly approach targetCameraZ
        float diff = this.targetCameraZ - this.cameraZ;
        if (Math.abs(diff) > 0.001f) {
            this.cameraZ += diff * 0.05f; // 5% closer each frame (3x slower than before)
            this.repaint();
        } else {
            this.cameraZ = this.targetCameraZ;
        }
    }

    @Override
    public void onRender(final Graphics2D g) {
        if (this.children == null) return;

        final Graphics2D g2dClip = (Graphics2D) g.create();
        g2dClip.setClip((int)this.getAbsoluteX(), (int)this.getAbsoluteY(), (int)this.width, (int)this.height);

        final float vx = this.getAbsoluteX() + this.width / 2f;
        final float vy = this.getAbsoluteY() + this.height / 2f;

        // Pre-update vanishing point for all children so they can calculate depth correctly
        for (final Component child : this.children) {
            if (child instanceof Spatial) {
                final Spatial s = (Spatial) child;
                s.setVanishingPoint(vx, vy);
                s.setCameraX(this.cameraX);
                s.setCameraY(this.cameraY);
                s.setCameraZ(this.cameraZ);
                s.setFocalLength(this.focalLength);
            }
        }

        final List<Component> sorted = new ArrayList<>(this.children);
        sorted.sort((c1, c2) -> {
            final float z1 = (c1 instanceof Spatial) ? ((Spatial) c1).getCalculatedDepth() : 0f;
            final float z2 = (c2 instanceof Spatial) ? ((Spatial) c2).getCalculatedDepth() : 0f;
            return Float.compare(z2, z1); 
        });

        for (final Component child : sorted) {
            child.render(g2dClip);
        }
        
        g2dClip.dispose();
    }

    @Override
    public void render(final Graphics2D g) {
        this.onRender(g);
    }

    public float getCameraX() { return this.cameraX; }
    public float getCameraY() { return this.cameraY; }
    public float getCameraZ() { return this.cameraZ; }
    public float getTargetCameraZ() { return this.targetCameraZ; }
    public float getFocalLength() { return this.focalLength; }

    public void setCameraX(final float x) { this.cameraX = x; this.repaint(); }
    public void setCameraY(final float y) { this.cameraY = y; this.repaint(); }
    public void setCameraZ(final float z) { this.targetCameraZ = z; this.repaint(); }
    public void setCameraZInstant(final float z) { this.cameraZ = z; this.targetCameraZ = z; this.repaint(); }
    public void setFocalLength(final float f) { this.focalLength = f; this.repaint(); }
}
