package fastui.composable;

import fastui.component.Component;
import fastui.behaviour.ScrollBarBehavior;
import fastui.component.Image3x3;

import java.awt.image.BufferedImage;

public class ScrollBar implements Composable {
    private final Image3x3 track;
    private final Image3x3 thumb;
    
    private float viewStart = 0f;
    private float viewEnd = 1f;

    public interface ScrollListener {
        void onScroll(float newStart, float newEnd);
    }
    private ScrollListener listener;

    public ScrollBar(final BufferedImage trackImg, final BufferedImage thumbImg) {
        this.track = new Image3x3(trackImg);
        this.thumb = new Image3x3(thumbImg);
        
        this.thumb.addBehavior(new ScrollBarBehavior(this.track, delta -> {
            final float span = viewEnd - viewStart;
            viewStart = Math.max(0, Math.min(1 - span, viewStart + delta));
            viewEnd = viewStart + span;
            
            updateThumb();
            if (listener != null) listener.onScroll(viewStart, viewEnd);
        }));
    }

    public void setBounds(final int x, final int y, final int w, final int h) {
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
        final int tw = Math.max(20, (int)((this.viewEnd - this.viewStart) * this.track.getWidth()));
        final int tx = this.track.getX() + (int)(this.viewStart * this.track.getWidth());
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
