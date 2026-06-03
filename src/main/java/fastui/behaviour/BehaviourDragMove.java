package fastui.behaviour;

import fastui.component.Component;
import java.util.function.BiConsumer;

/**
 * Generic drag behaviour that reports incremental (deltaX, deltaY) on each drag frame.
 * Wire to a handle component; use the callback to move whatever needs moving.
 */
public class BehaviourDragMove implements Behaviour {

    private final BiConsumer<Float, Float> onDrag;
    private float startMx, startMy;

    public BehaviourDragMove(BiConsumer<Float, Float> onDrag) {
        this.onDrag = onDrag;
    }

    @Override
    public void onMousePressed(Component target, float mx, float my) {
        startMx = mx;
        startMy = my;
    }

    @Override
    public void onMouseDragged(Component target, float mx, float my) {
        onDrag.accept(mx - startMx, my - startMy);
        startMx = mx;
        startMy = my;
    }
}
