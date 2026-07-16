package fastui.behaviour;

import fastui.component.Component;
import fastui.component.TextDisplay;

import java.awt.event.KeyEvent;

public class BehaviorChatInput implements Behaviour {
    private final Runnable onSendCallback;

    public BehaviorChatInput(final Runnable onSendCallback) {
        this.onSendCallback = onSendCallback;
    }

    @Override
    public void onKeyPressed(final Component target, final KeyEvent e) {
        if (target instanceof TextDisplay) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (!e.isShiftDown()) {
                    e.consume(); // Prevent default text input newline
                    if (onSendCallback != null) {
                        onSendCallback.run();
                    }
                }
            }
        }
    }
}
