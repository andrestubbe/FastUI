package fastui.behaviour;

import fastui.component.Component;
import fastui.component.Image3x3;

import java.util.ArrayList;
import java.util.List;

public class RangeBehavior3x3 implements MouseBehavior {
    public interface RangeListener {
        void onRangeChanged(float min, float max);
    }

    private enum DragState { NONE, MIN, MAX, WHOLE }

    private float minValue;
    private float maxValue;
    private DragState drag = DragState.NONE;
    private float dragOffset = 0f;
    private final int edgeThreshold = 20;
    private final List<RangeListener> listeners = new ArrayList<>();

    public RangeBehavior3x3(final float minValue, final float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void onMousePressed(final Component target, final int mx, final int my) {
        final int minX = target.getX() + (int)(this.minValue * target.getWidth());
        final int maxX = target.getX() + (int)(this.maxValue * target.getWidth());

        if (mx <= minX + this.edgeThreshold && mx >= minX - this.edgeThreshold) {
            this.drag = DragState.MIN;
        } else if (mx <= maxX + this.edgeThreshold && mx >= maxX - this.edgeThreshold) {
            this.drag = DragState.MAX;
        } else if (mx < minX) {
            this.minValue = Math.max(0, (float)(mx - target.getX()) / target.getWidth());
            this.drag = DragState.MIN;
            ((Image3x3) target).setRange(this.minValue, this.maxValue);
        } else if (mx > maxX) {
            this.maxValue = Math.min(1, (float)(mx - target.getX()) / target.getWidth());
            this.drag = DragState.MAX;
            ((Image3x3) target).setRange(this.minValue, this.maxValue);
        } else {
            this.drag = DragState.WHOLE;
            this.dragOffset = (float)(mx - target.getX()) / target.getWidth() - this.minValue;
        }
    }

    @Override
    public void onMouseReleased(final Component target, final int mx, final int my) {
        this.drag = DragState.NONE;
    }

    @Override
    public void onMouseDragged(final Component target, final int mx, final int my) {
        if (this.drag == DragState.NONE) return;
        final float v = Math.max(0, Math.min(1, (float)(mx - target.getX()) / target.getWidth()));
        if (this.drag == DragState.MIN) {
            this.minValue = Math.min(v, this.maxValue - 0.01f);
        } else if (this.drag == DragState.MAX) {
            this.maxValue = Math.max(v, this.minValue + 0.01f);
        } else if (this.drag == DragState.WHOLE) {
            final float span = this.maxValue - this.minValue;
            this.minValue = Math.max(0, Math.min(1 - span, v - this.dragOffset));
            this.maxValue = this.minValue + span;
        }
        ((Image3x3) target).setRange(this.minValue, this.maxValue);
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
