package fastui.model;

import java.util.ArrayList;
import java.util.List;

public class TimelineViewport {

    public interface ViewportListener {
        void onViewportChanged();
        void onSelectionChanged(final float min, final float max);
    }

    private final long absoluteStart;
    private final long absoluteEnd;
    private final List<ViewportListener> listeners = new ArrayList<>();

    private long viewStart;
    private long viewEnd;
    private float selectionMin = 0.2f;
    private float selectionMax = 0.8f;

    public TimelineViewport(final long absoluteStart, final long absoluteEnd) {
        this.absoluteStart = absoluteStart;
        this.absoluteEnd = absoluteEnd;
        this.viewStart = absoluteStart;
        this.viewEnd = absoluteEnd;
    }

    public void addListener(final ViewportListener l) {
        this.listeners.add(l);
    }

    public void setView(final long start, final long end) {
        this.viewStart = Math.max(this.absoluteStart, Math.min(start, this.absoluteEnd));
        this.viewEnd = Math.max(this.viewStart + 1000, Math.min(end, this.absoluteEnd));
        this.notifyViewport();
    }

    public void pan(final long deltaMs) {
        final long span = this.viewEnd - this.viewStart;
        final long newStart = Math.max(this.absoluteStart, Math.min(this.viewStart + deltaMs, this.absoluteEnd - span));
        this.viewStart = newStart;
        this.viewEnd = newStart + span;
        this.notifyViewport();
    }

    public void setSelection(final float min, final float max) {
        this.selectionMin = Math.max(0, Math.min(1, min));
        this.selectionMax = Math.max(this.selectionMin, Math.min(1, max));
        this.notifySelection();
    }

    private void notifyViewport() {
        for (final ViewportListener l : this.listeners) l.onViewportChanged();
    }

    private void notifySelection() {
        for (final ViewportListener l : this.listeners) l.onSelectionChanged(this.selectionMin, this.selectionMax);
    }

    public long getAbsoluteStart() { return this.absoluteStart; }
    public long getAbsoluteEnd() { return this.absoluteEnd; }
    public long getViewStart() { return this.viewStart; }
    public long getViewEnd() { return this.viewEnd; }
    public float getSelectionMin() { return this.selectionMin; }
    public float getSelectionMax() { return this.selectionMax; }
    
    public long getSelectionStartTime() {
        return this.viewStart + (long)(this.selectionMin * (this.viewEnd - this.viewStart));
    }
    
    public long getSelectionEndTime() {
        return this.viewStart + (long)(this.selectionMax * (this.viewEnd - this.viewStart));
    }
}
