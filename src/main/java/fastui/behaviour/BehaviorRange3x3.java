package fastui.behaviour;

import fastui.component.Component;

import java.util.ArrayList;
import java.util.List;

public class BehaviorRange3x3 implements Behaviour {

    public interface RangeListener {
        void onRangeChanged(final float min, final float max);
    }

    private enum DragState { NONE, MIN, MAX, WHOLE }

    private final int edgeThreshold = 20;
    private final List<RangeListener> listeners = new ArrayList<>();

    private float minValue;
    private float maxValue;
    private DragState drag = DragState.NONE;
    private float dragOffset = 0f;

    public BehaviorRange3x3(final float minValue, final float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void onMousePressed(final Component target, final float mx, final float my) {
        final float minX = target.getX() + (this.minValue * target.getWidth());
        final float maxX = target.getX() + (this.maxValue * target.getWidth());

        if (mx <= minX + this.edgeThreshold && mx >= minX - this.edgeThreshold) {
            this.drag = DragState.MIN;
        } else if (mx <= maxX + this.edgeThreshold && mx >= maxX - this.edgeThreshold) {
            this.drag = DragState.MAX;
        } else if (mx < minX) {
            this.minValue = Math.max(0, (mx - target.getX()) / target.getWidth());
            this.drag = DragState.MIN;
        } else if (mx > maxX) {
            this.maxValue = Math.min(1, (mx - target.getX()) / target.getWidth());
            this.drag = DragState.MAX;
        } else {
            this.drag = DragState.WHOLE;
            this.dragOffset = (mx - target.getX()) / target.getWidth() - this.minValue;
        }
        this.notifyListeners();
    }

    @Override
    public void onMouseReleased(final Component target, final float mx, final float my) {
        this.drag = DragState.NONE;
    }

    @Override
    public void onMouseDragged(final Component target, final float mx, final float my) {
        if (this.drag == DragState.NONE) return;
        final float v = Math.max(0, Math.min(1, (mx - target.getX()) / target.getWidth()));
        if (this.drag == DragState.MIN) {
            this.minValue = Math.min(v, this.maxValue - 0.01f);
        } else if (this.drag == DragState.MAX) {
            this.maxValue = Math.max(v, this.minValue + 0.01f);
        } else if (this.drag == DragState.WHOLE) {
            final float span = this.maxValue - this.minValue;
            this.minValue = Math.max(0, Math.min(1 - span, v - this.dragOffset));
            this.maxValue = this.minValue + span;
        }
        this.notifyListeners();
    }

    public void setRange(final float min, final float max) {
        this.minValue = min;
        this.maxValue = max;
        this.notifyListeners();
    }

    public void addListener(final RangeListener listener) {
        this.listeners.add(listener);
    }

    private void notifyListeners() {
        for (final RangeListener l : this.listeners) l.onRangeChanged(this.minValue, this.maxValue);
    }

    public float getMinValue() { return this.minValue; }
    public float getMaxValue() { return this.maxValue; }
}
