package fastui.component;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Spatial extends Component {

    private final Component content;
    private float z;
    private float cameraX = 0f;
    private float cameraY = 0f;
    private float cameraZ = 0f;
    private float vanishingX = 0f;
    private float vanishingY = 0f;
    private float focalLength = 15.0f;
    private Color fogColor = new Color(15, 15, 15);

    private BufferedImage textureCache;
    private List<BufferedImage> mipmaps;
    private boolean mipmappingEnabled = false;

    private Component contentHovered = null;
    private Component contentActive = null;

    private float rotationY = 0f;
    private float worldRotationY = 0f;
    private int sliceCount = 40; // Papervision3D style slicing

    // Snapshots for interaction sync
    private float lastScale = 1.0f;
    private float lastPx = 0;
    private float lastPy = 0;
    private float lastVx = 0;
    private float lastVy = 0;
    private float lastRotY = 0f;
    private float lastWorldRotY = 0f;

    public Spatial(final Component content) {
        this.content = content;
        this.content.parent = this;
        content.setRoot(this.root);
    }

    public void setFogColor(final Color color) {
        this.fogColor = color;
    }

    public void setMipmappingEnabled(final boolean enabled) {
        this.mipmappingEnabled = enabled;
        this.textureCache = null;
        this.mipmaps = null;
        this.repaint();
    }

    @Override
    public void onRender(final Graphics2D g) {
        final float depth = this.z - this.cameraZ;
        if (depth <= -this.focalLength + 1.0f) return;

        final float scale = this.focalLength / (this.focalLength + depth);
        // Softer fog: 0.001f instead of 0.1f for a more gradual gradient
        final float darkness = 1.0f / (1.0f + (float) Math.pow(Math.max(0, depth * 0.0015f), 1.2f));

        // Pan
        final float px = -this.cameraX * scale;
        final float py = -this.cameraY * scale;

        // Perspective position
        final float vx = (this.vanishingX - this.vanishingX * scale) + px;
        final float vy = (this.vanishingY - this.vanishingY * scale) + py;

        this.lastScale = scale;
        this.lastPx = px;
        this.lastPy = py;
        this.lastVx = this.vanishingX;
        this.lastVy = this.vanishingY;
        this.lastRotY = this.rotationY;
        this.lastWorldRotY = this.worldRotationY;

        if (this.textureCache == null && !this.mipmappingEnabled) {
            this.updateCache();
        } else if (this.mipmaps == null && this.mipmappingEnabled) {
            this.updateCache();
        }

        final Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (Math.abs(this.rotationY) < 0.1f && Math.abs(this.worldRotationY) < 0.1f) {
            // Fast Path: Standard Affine projection
            g2d.translate(this.getAbsoluteX() * scale + vx, this.getAbsoluteY() * scale + vy);
            if (darkness < 1.0f) {
                final float[] scales = {darkness, darkness, darkness, 1.0f};
                final float[] offsets = {
                        this.fogColor.getRed() * (1.0f - darkness),
                        this.fogColor.getGreen() * (1.0f - darkness),
                        this.fogColor.getBlue() * (1.0f - darkness),
                        0f
                };
                final java.awt.image.RescaleOp fogOp = new java.awt.image.RescaleOp(scales, offsets, g2d.getRenderingHints());

                if (this.mipmappingEnabled && this.mipmaps != null && !this.mipmaps.isEmpty()) {
                    int level = (int) (Math.log(1.0 / Math.min(1.0, scale)) / Math.log(2.0));
                    level = Math.max(0, Math.min(level, this.mipmaps.size() - 1));
                    float mipScaleFactor = (float) Math.pow(2.0, level);
                    g2d.scale(scale * mipScaleFactor, scale * mipScaleFactor);
                    g2d.drawImage(this.mipmaps.get(level), fogOp, 0, 0);
                } else if (this.textureCache != null) {
                    g2d.scale(scale, scale);
                    g2d.drawImage(this.textureCache, fogOp, 0, 0);
                }
            } else {
                if (this.mipmappingEnabled && this.mipmaps != null && !this.mipmaps.isEmpty()) {
                    int level = (int) (Math.log(1.0 / Math.min(1.0, scale)) / Math.log(2.0));
                    level = Math.max(0, Math.min(level, this.mipmaps.size() - 1));
                    float mipScaleFactor = (float) Math.pow(2.0, level);
                    g2d.scale(scale * mipScaleFactor, scale * mipScaleFactor);
                    g2d.drawImage(this.mipmaps.get(level), 0, 0, null);
                } else if (this.textureCache != null) {
                    g2d.scale(scale, scale);
                    g2d.drawImage(this.textureCache, 0, 0, null);
                }
            }
        } else {
            // 3D Path: World Orbit + Local Slicing
            final float wRad = (float) Math.toRadians(this.worldRotationY);
            final float wCos = (float) Math.cos(wRad);
            final float wSin = (float) Math.sin(wRad);

            final float lRad = (float) Math.toRadians(this.rotationY);

            final float halfW = this.width / 2f;
            final float halfH = this.height / 2f;
            final float sliceW = this.width / this.sliceCount;

            // 1. Calculate Orbital Center in 3D (relative to vanishing point)
            final float worldCenterX = this.getAbsoluteX() + halfW - this.vanishingX;
            final float worldCenterZ = depth;

            final float rotWorldX = worldCenterX * wCos - worldCenterZ * wSin;
            final float rotWorldZ = worldCenterX * wSin + worldCenterZ * wCos;

            for (int i = 0; i < this.sliceCount; i++) {
                final float lx0 = i * sliceW;
                final float lx1 = (i + 1) * sliceW;

                // 2. Combined Rotation (World + Local)
                final float totalRad = lRad + wRad;
                final float tCos = (float) Math.cos(totalRad);
                final float tSin = (float) Math.sin(totalRad);

                final float rx0 = (lx0 - halfW) * tCos;
                final float rz0 = (lx0 - halfW) * tSin;
                final float rx1 = (lx1 - halfW) * tCos;
                final float rz1 = (lx1 - halfW) * tSin;

                // 3. Final World Position relative to vanishing point
                final float fx0 = rotWorldX + rx0;
                final float fz0 = rotWorldZ + rz0;
                final float fx1 = rotWorldX + rx1;
                final float fz1 = rotWorldZ + rz1;

                // 4. Perspective Projection
                final float s0 = this.focalLength / (this.focalLength + fz0);
                final float s1 = this.focalLength / (this.focalLength + fz1);

                // Per-slice pan (camera feeling)
                final float px0 = -this.cameraX * s0;
                final float py0 = -this.cameraY * s0;
                final float px1 = -this.cameraX * s1;
                final float py1 = -this.cameraY * s1;

                final float sx0 = this.vanishingX + fx0 * s0 + px0;
                final float sy0 = this.vanishingY + (this.getAbsoluteY() + halfH - this.vanishingY) * s0 + py0 - halfH * s0;
                final float sh0 = this.height * s0;

                final float sx1 = this.vanishingX + fx1 * s1 + px1;
                final float sy1 = this.vanishingY + (this.getAbsoluteY() + halfH - this.vanishingY) * s1 + py1 - halfH * s1;
                final float sh1 = this.height * s1;

                // Draw the textured slice
                g2d.drawImage(this.textureCache,
                        (int) sx0, (int) sy0, (int) sx1, (int) (sy0 + sh0),
                        (int) lx0, 0, (int) lx1, (int) this.height,
                        null);

                // 5. 3D Fog Overlay (High-Performance)
                // Use a more stable fog calculation relative to screen depth
                float sliceDarkness = 1.0f / (1.0f + (float) Math.pow(Math.max(0, fz0 * 0.0012f), 1.3f));
                if (sliceDarkness < 0.99f) {
                    float alpha = 1.0f - sliceDarkness;
                    g2d.setColor(new Color(
                            this.fogColor.getRed() / 255f,
                            this.fogColor.getGreen() / 255f,
                            this.fogColor.getBlue() / 255f,
                            alpha));
                    // Fill the slice area on screen
                    g2d.fillRect((int) sx0, (int) sy0, (int) (sx1 - sx0 + 1), (int) sh0 + 1);
                }
            }
        }

        g2d.dispose();
    }

    private void updateCache() {
        if (this.width <= 0 || this.height <= 0) return;

        final BufferedImage base = new BufferedImage((int) this.width, (int) this.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g2 = base.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.translate(-this.content.getAbsoluteX(), -this.content.getAbsoluteY());
        this.content.render(g2);
        g2.dispose();

        if (this.mipmappingEnabled) {
            this.mipmaps = new ArrayList<>();
            this.mipmaps.add(base);

            int currW = (int) this.width;
            int currH = (int) this.height;
            BufferedImage prev = base;

            while (currW > 16 && currH > 16) {
                currW /= 2;
                currH /= 2;
                final BufferedImage next = new BufferedImage(currW, currH, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D gn = next.createGraphics();
                gn.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                gn.drawImage(prev, 0, 0, currW, currH, null);
                gn.dispose();
                this.mipmaps.add(next);
                prev = next;
            }
        }

        this.textureCache = base;
    }

    public float getCalculatedDepth() {
        // MUST match onRender orbital logic exactly
        final float wRad = (float) Math.toRadians(this.worldRotationY);
        final float wCos = (float) Math.cos(wRad);
        final float wSin = (float) Math.sin(wRad);

        final float halfW = this.width / 2f;
        final float worldCenterX = this.getAbsoluteX() + halfW - this.vanishingX;
        final float worldCenterZ = this.z + this.cameraZ;

        // The Z-depth from the camera plane (fz0 in onRender)
        return worldCenterX * wSin + worldCenterZ * wCos;
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(final float z) {
        this.z = z;
    }

    public float getRotationY() {
        return this.rotationY;
    }

    public void setRotationY(final float rotationY) {
        this.rotationY = rotationY;
    }

    public float getWorldRotationY() {
        return this.worldRotationY;
    }

    public void setWorldRotationY(final float worldRotationY) {
        this.worldRotationY = worldRotationY;
    }

    public int getSliceCount() {
        return this.sliceCount;
    }

    public void setSliceCount(final int sliceCount) {
        this.sliceCount = sliceCount;
    }

    public void setCameraX(final float cameraX) {
        this.cameraX = cameraX;
    }

    public void setCameraY(final float cameraY) {
        this.cameraY = cameraY;
    }

    public void setCameraZ(final float cameraZ) {
        this.cameraZ = cameraZ;
    }

    @Override
    public void render(final Graphics2D g) {
        this.onRender(g);
    }

    @Override
    public List<Component> getChildren() {
        // Return empty list so InteractionManager doesn't try to hit-test children
        // with raw screen coordinates. We handle event propagation ourselves.
        return new ArrayList<>(0);
    }

    private Point2D.Float toLocal(final float mx, final float my) {
        if (Math.abs(this.lastRotY) < 0.1f && Math.abs(this.lastWorldRotY) < 0.1f) {
            final float px = this.lastPx;
            final float py = this.lastPy;
            final float scale = this.lastScale;
            final float vx = this.lastVx;
            final float vy = this.lastVy;

            // Transform mouse point to local space relative to vanishing point
            final float lx = (mx - px - vx) / scale + vx - this.getAbsoluteX();
            final float ly = (my - py - vy) / scale + vy - this.getAbsoluteY();

            return new Point2D.Float(lx, ly);
        } else {
            // 3D Inverse Orbital Projection
            final float wRad = (float) Math.toRadians(this.lastWorldRotY);
            final float wCos = (float) Math.cos(wRad);
            final float wSin = (float) Math.sin(wRad);

            final float lRad = (float) Math.toRadians(this.lastRotY);
            final float totalRad = lRad + wRad;
            final float tCos = (float) Math.cos(totalRad);
            final float tSin = (float) Math.sin(totalRad);

            final float halfW = this.width / 2f;
            final float halfH = this.height / 2f;
            final float focal = this.focalLength;

            // Orbital Center
            final float worldCenterX = this.getAbsoluteX() + halfW - this.lastVx;
            final float worldCenterZ = this.z;
            final float rotWorldX = worldCenterX * wCos - worldCenterZ * wSin;
            final float rotWorldZ = worldCenterX * wSin + worldCenterZ * wCos;

            // Screen relative to vanishing point + pan
            final float A = mx - this.lastVx - this.lastPx;

            // Derived formula for dx (localX - halfW)
            final float dx = (focal * rotWorldX - A * (focal + rotWorldZ)) / (A * tSin - focal * tCos);
            final float lx = dx + halfW;

            // Re-project at this X to find local Y
            final float rz = dx * tSin;
            final float s = focal / (focal + rotWorldZ + rz);

            final float sy_vy_py = my - this.lastVy - this.lastPy;
            // From sy0 formula: sy0 = vanishingY + (worldCenterY - vanishingY) * s + py - halfH * s
            final float worldCenterY = this.getAbsoluteY() + halfH;
            final float ly = (sy_vy_py) / s + this.lastVy - (worldCenterY - halfH);

            return new Point2D.Float(lx, ly);
        }
    }

    @Override
    public void onMousePressed(final float mx, final float my) {
        super.onMousePressed(mx, my);
        final Point2D.Float local = this.toLocal(mx, my);

        final Component hit = this.findChildRecursive(this.content, local.x, local.y);
        this.contentActive = hit != null ? hit : this.content;
        this.contentActive.onMousePressed(local.x, local.y);

        this.textureCache = null;
        this.mipmaps = null;
    }

    @Override
    public void onMouseMoved(final float mx, final float my) {
        super.onMouseMoved(mx, my);
        final Point2D.Float local = this.toLocal(mx, my);

        final Component hit = this.findChildRecursive(this.content, local.x, local.y);
        final Component target = hit != null ? hit : this.content;

        boolean changed = false;
        if (target != this.contentHovered) {
            if (this.contentHovered != null) this.contentHovered.onMouseExit();
            if (target != null) target.onMouseEnter();
            this.contentHovered = target;
            changed = true;
        }

        if (this.contentHovered != null) {
            this.contentHovered.onMouseMoved(local.x, local.y);
        }

        if (changed) {
            this.textureCache = null;
            this.mipmaps = null;
        }
    }

    @Override
    public void onMouseDragged(final float mx, final float my) {
        super.onMouseDragged(mx, my);
        final Point2D.Float local = this.toLocal(mx, my);
        if (this.contentActive != null) {
            this.contentActive.onMouseDragged(local.x, local.y);
            this.textureCache = null;
            this.mipmaps = null;
        }
    }

    @Override
    public void onMouseReleased(final float mx, final float my) {
        super.onMouseReleased(mx, my);
        final Point2D.Float local = this.toLocal(mx, my);
        if (this.contentActive != null) {
            this.contentActive.onMouseReleased(local.x, local.y);
            this.contentActive = null;
            this.textureCache = null;
            this.mipmaps = null;
        }
    }

    @Override
    public void onMouseExit() {
        super.onMouseExit();
        if (this.contentHovered != null) {
            this.contentHovered.onMouseExit();
            this.contentHovered = null;
            this.textureCache = null;
            this.mipmaps = null;
        }
    }

    private Component findChildRecursive(final Component root, final float lx, final float ly) {
        if (root.getChildren() == null) return null;
        for (int i = root.getChildren().size() - 1; i >= 0; i--) {
            final Component child = root.getChildren().get(i);
            if (!child.isHitTestable()) continue;

            if (child.contains(lx, ly)) {
                final Component sub = this.findChildRecursive(child, lx, ly);
                if (sub != null) return sub;
                return child;
            }
        }
        return null;
    }

    @Override
    public boolean contains(final float mx, final float my) {
        final Point2D.Float local = this.toLocal(mx, my);
        final float lx = local.x;
        final float ly = local.y;

        final float ax = this.content.getAbsoluteX();
        final float ay = this.content.getAbsoluteY();

        return lx >= ax && lx <= ax + this.width && ly >= ay && ly <= ay + this.height;
    }

    public void setVanishingPoint(final float vx, final float vy) {
        this.vanishingX = vx;
        this.vanishingY = vy;
    }

    public void setFocalLength(final float f) {
        this.focalLength = f;
    }

    @Override
    public void setBounds(final float x, final float y, final float width, final float height) {
        super.setBounds(x, y, width, height);
        this.content.setBounds(0, 0, width, height);
    }
}
