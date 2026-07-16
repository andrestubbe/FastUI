package fastui.composable;

import fastui.component.Component;
import fastui.behaviour.BehaviorScrollBar;
import fastui.component.Image9Slice;

import java.awt.image.BufferedImage;

public class ScrollBar implements Composable {
    private final Image9Slice track;
    private final Image9Slice thumb;
    
    private float viewStart = 0f;
    private float viewEnd = 1f;

    public interface ScrollListener {
        void onScroll(float newStart, float newEnd);
    }
    private ScrollListener listener;

    public ScrollBar(final BufferedImage trackImg, final BufferedImage thumbImg) {
        int arc = trackImg.getHeight() / 2;
        this.track = new Image9Slice(arc, arc, 0, 0, trackImg);
        this.thumb = new Image9Slice(arc, arc, 0, 0, thumbImg);
        
        this.thumb.addBehavior(new BehaviorScrollBar(this.track, delta -> {
            final float span = viewEnd - viewStart;
            viewStart = Math.max(0, Math.min(1 - span, viewStart + delta));
            viewEnd = viewStart + span;
            
            updateThumb();
            if (listener != null) listener.onScroll(viewStart, viewEnd);
        }));
    }

    public void setBounds(final float x, final float y, final float w, final float h) {
        this.track.setBounds(x, y, w, h);
        this.updateThumb();
    }

    public void setView(final float start, final float end) {
        this.viewStart = Math.max(0, Math.min(1, start));
        this.viewEnd = Math.max(this.viewStart + 0.01f, Math.min(1, end));
        this.updateThumb();
    }

    private void updateThumb() {
        if (this.track.getWidth() <= 0) return;
        final float tw = Math.max(20f, (this.viewEnd - this.viewStart) * this.track.getWidth());
        final float tx = this.track.getX() + (this.viewStart * this.track.getWidth());
        this.thumb.setBounds(tx, this.track.getY(), tw, this.track.getHeight());
    }

    public void setScrollListener(final ScrollListener l) {
        this.listener = l;
    }

    @Override
    public Component[] components() {
        return new Component[]{this.track, this.thumb};
    }
}
