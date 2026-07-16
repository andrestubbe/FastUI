package fastui.behaviour;

import fastui.component.Component;
import fastui.component.TextDisplay;

import java.awt.event.KeyEvent;

public class BehaviorEditable implements Behaviour {

    @Override
    public void onKeyPressed(final Component target, final KeyEvent e) {
        if (target instanceof TextDisplay) {
            final TextDisplay display = (TextDisplay) target;
            
            if (e.isControlDown()) {
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    String text = display.getText();
                    java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new java.awt.datatransfer.StringSelection(text), null);
                    e.consume();
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_V) {
                    if (display.hasSelection()) {
                        display.deleteSelection();
                    }
                    try {
                        String text = (String) java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().getData(java.awt.datatransfer.DataFlavor.stringFlavor);
                        if (text != null) {
                            display.append(text);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    e.consume();
                    return;
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    display.selectAll();
                    e.consume();
                    return;
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                if (display.hasSelection()) {
                    display.deleteSelection();
                } else {
                    display.deleteLastChar();
                }
                e.consume();
            } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (display.hasSelection()) {
                    display.deleteSelection();
                }
                display.insertNewLine();
                e.consume();
            }
        }
    }

    @Override
    public void onKeyTyped(final Component target, final KeyEvent e) {
        if (target instanceof TextDisplay) {
            final TextDisplay display = (TextDisplay) target;
            char c = e.getKeyChar();
            if (c == KeyEvent.CHAR_UNDEFINED || c == '\b' || c == '\n') return;
            
            if (display.hasSelection()) {
                display.deleteSelection();
            }
            display.insertChar(c);
            e.consume();
        }
    }
}
