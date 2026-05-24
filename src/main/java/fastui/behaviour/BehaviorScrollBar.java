package fastui.behaviour;


import fastui.component.Component;

public class BehaviorScrollBar implements Behaviour {
    public interface ScrollListener {
        void onScroll(float delta);
    }

    private final Component track;
    private final ScrollListener listener;

    private boolean dragging = false;
    private float dragStartX = 0f;

    public BehaviorScrollBar(final Component track, final ScrollListener listener) {
        this.track = track;
        this.listener = listener;
    }

    @Override
    public void onMousePressed(final Component target, final float mx, final float my) {
        this.dragging = true;
        this.dragStartX = mx;
    }

    @Override
    public void onMouseReleased(final Component target, final float mx, final float my) {
        this.dragging = false;
    }

    @Override
    public void onMouseDragged(final Component target, final float mx, final float my) {
        if (!this.dragging || this.track.getWidth() <= 0) return;

        final float delta = (mx - this.dragStartX) / this.track.getWidth();
        this.dragStartX = mx;
        if (this.listener != null) this.listener.onScroll(delta);
    }
}
