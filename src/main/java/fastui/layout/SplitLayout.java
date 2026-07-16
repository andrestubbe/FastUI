package fastui.layout;

import fastui.component.Component;

public class SplitLayout implements Layout {
    public static final SplitLayout INSTANCE = new SplitLayout();

    public static final int FLAG_VERTICAL = 1;
    public static final int FLAG_FROM_END = 2;

    private SplitLayout() {}

    @Override
    public void apply(Component parent) {
        Component first = parent.firstChild;
        if (first == null) return;
        Component handle = first.nextSibling;
        if (handle == null) return;
        Component second = handle.nextSibling;

        boolean isVertical = (parent.layoutFlags & FLAG_VERTICAL) != 0;
        boolean fromEnd = (parent.layoutFlags & FLAG_FROM_END) != 0;
        float splitPos = parent.layoutA;

        float parentW = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        float parentH = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();

        if (second == null) {
            // Fallback if there is no third handle component: treat the second child as the end component
            second = handle;
            handle = null;
        }

        float dividerSize = 0;
        if (handle != null) {
            dividerSize = isVertical ? handle.preferredHeight : handle.preferredWidth;
            if (dividerSize <= 0) {
                dividerSize = isVertical ? handle.getHeight() : handle.getWidth();
            }
            if (dividerSize <= 0) dividerSize = 4; // default size
        }

        if (!isVertical) {
            // Horizontal split (left and right)
            float splitX;
            if (first != null && !first.isVisible()) {
                splitX = 0;
            } else if (second != null && !second.isVisible()) {
                splitX = parentW;
            } else {
                if (fromEnd) {
                    splitX = parentW - splitPos;
                } else {
                    splitX = splitPos;
                }
            }

            if (first.isVisible()) {
                float w1 = splitX - first.marginLeft - first.marginRight - dividerSize / 2f;
                if (w1 < 0) w1 = 0;
                first.setBounds(
                    parent.getPaddingLeft() + first.marginLeft,
                    parent.getPaddingTop() + first.marginTop,
                    w1,
                    parentH - first.marginTop - first.marginBottom
                );
            }

            if (handle != null && handle.isVisible()) {
                handle.setBounds(
                    parent.getPaddingLeft() + splitX - dividerSize / 2f,
                    parent.getPaddingTop(),
                    dividerSize,
                    parentH
                );
            }

            if (second.isVisible()) {
                float w2 = parentW - splitX - second.marginLeft - second.marginRight - dividerSize / 2f;
                if (w2 < 0) w2 = 0;
                second.setBounds(
                    parent.getPaddingLeft() + splitX + dividerSize / 2f + second.marginLeft,
                    parent.getPaddingTop() + second.marginTop,
                    w2,
                    parentH - second.marginTop - second.marginBottom
                );
            }
        } else {
            // Vertical split (top and bottom)
            float splitY;
            if (first != null && !first.isVisible()) {
                splitY = 0;
            } else if (second != null && !second.isVisible()) {
                splitY = parentH;
            } else {
                if (fromEnd) {
                    splitY = parentH - splitPos;
                } else {
                    splitY = splitPos;
                }
            }

            if (first.isVisible()) {
                float h1 = splitY - first.marginTop - first.marginBottom - dividerSize / 2f;
                if (h1 < 0) h1 = 0;
                first.setBounds(
                    parent.getPaddingLeft() + first.marginLeft,
                    parent.getPaddingTop() + first.marginTop,
                    parentW - first.marginLeft - first.marginRight,
                    h1
                );
            }

            if (handle != null && handle.isVisible()) {
                handle.setBounds(
                    parent.getPaddingLeft(),
                    parent.getPaddingTop() + splitY - dividerSize / 2f,
                    parentW,
                    dividerSize
                );
            }

            if (second.isVisible()) {
                float h2 = parentH - splitY - second.marginTop - second.marginBottom - dividerSize / 2f;
                if (h2 < 0) h2 = 0;
                second.setBounds(
                    parent.getPaddingLeft() + second.marginLeft,
                    parent.getPaddingTop() + splitY + dividerSize / 2f + second.marginTop,
                    parentW - second.marginLeft - second.marginRight,
                    h2
                );
            }
        }
    }
}
