package fastui.layout;

import fastui.component.Component;

public class LinearLayout implements Layout {
    public static final LinearLayout INSTANCE = new LinearLayout();

    public static final int AXIS_HORIZONTAL = 0;
    public static final int AXIS_VERTICAL = 1;

    private LinearLayout() {}

    @Override
    public void apply(Component parent) {
        boolean isHorizontal = (parent.layoutFlags == AXIS_HORIZONTAL);
        float gap = parent.layoutB;

        float totalWeight = 0;
        float constantSize = 0;
        float gapAccum = 0;
        boolean first = true;

        Component child = parent.firstChild;
        while (child != null) {
            if (child.isVisible()) {
                if (!first) {
                    gapAccum += gap;
                }
                first = false;

                if (child.weight > 0) {
                    totalWeight += child.weight;
                    if (isHorizontal) {
                        constantSize += child.marginLeft + child.marginRight;
                    } else {
                        constantSize += child.marginTop + child.marginBottom;
                    }
                } else {
                    if (isHorizontal) {
                        float preferredW = child.preferredWidth > 0 ? child.preferredWidth : child.getWidth();
                        constantSize += preferredW + child.marginLeft + child.marginRight;
                    } else {
                        float preferredH = child.preferredHeight > 0 ? child.preferredHeight : child.getHeight();
                        constantSize += preferredH + child.marginTop + child.marginBottom;
                    }
                }
            }
            child = child.nextSibling;
        }

        float available;
        if (isHorizontal) {
            available = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        } else {
            available = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        }

        float remainingSpace = available - constantSize - gapAccum;
        if (remainingSpace < 0) remainingSpace = 0;

        float currentPos = isHorizontal ? parent.getPaddingLeft() : parent.getPaddingTop();
        first = true;

        child = parent.firstChild;
        while (child != null) {
            if (child.isVisible()) {
                if (!first) {
                    currentPos += gap;
                }
                first = false;

                if (isHorizontal) {
                    float w;
                    if (child.weight > 0 && totalWeight > 0) {
                        w = (child.weight / totalWeight) * remainingSpace;
                    } else {
                        w = child.preferredWidth > 0 ? child.preferredWidth : child.getWidth();
                    }

                    if (child.minWidth > 0 && w < child.minWidth) w = child.minWidth;
                    if (child.maxWidth > 0 && w > child.maxWidth) w = child.maxWidth;

                    float childX = currentPos + child.marginLeft;
                    float childY = parent.getPaddingTop() + child.marginTop;
                    float childH = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom() - child.marginTop - child.marginBottom;
                    if (childH < 0) childH = 0;

                    child.setBounds(childX, childY, w, childH);
                    currentPos += w + child.marginLeft + child.marginRight;
                } else {
                    float h;
                    if (child.weight > 0 && totalWeight > 0) {
                        h = (child.weight / totalWeight) * remainingSpace;
                    } else {
                        h = child.preferredHeight > 0 ? child.preferredHeight : child.getHeight();
                    }

                    if (child.minHeight > 0 && h < child.minHeight) h = child.minHeight;
                    if (child.maxHeight > 0 && h > child.maxHeight) h = child.maxHeight;

                    float childX = parent.getPaddingLeft() + child.marginLeft;
                    float childY = currentPos + child.marginTop;
                    float childW = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight() - child.marginLeft - child.marginRight;
                    if (childW < 0) childW = 0;

                    child.setBounds(childX, childY, childW, h);
                    currentPos += h + child.marginTop + child.marginBottom;
                }
            }
            child = child.nextSibling;
        }
    }
}
