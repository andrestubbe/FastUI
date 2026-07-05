package fastui.composable;

import fastui.Container;
import fastui.component.Component;

import javax.swing.JWindow;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class Popup {
    private final JWindow window;
    private final Container container;
    private final java.awt.Window owner;
    private final ComponentAdapter ownerListener;

    public Popup(final java.awt.Window owner) {
        this.owner = owner;
        this.window = new JWindow(owner);
        this.window.setBackground(new Color(0, 0, 0, 0));
        this.window.setAlwaysOnTop(true);
        this.window.setFocusableWindowState(true);
        
        this.container = new Container();
        this.container.setBackground(new Color(0, 0, 0, 0));
        this.window.setContentPane(this.container);
        
        this.window.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {}

            @Override
            public void windowLostFocus(WindowEvent e) {
                hide();
            }
        });

        this.ownerListener = new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                hide();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                hide();
            }
        };
        this.owner.addComponentListener(this.ownerListener);
    }

    public void add(Component component) {
        this.container.add(component);
    }

    public void add(Composable composable) {
        this.container.add(composable);
    }

    public void show(int screenX, int screenY, int width, int height) {
        this.window.setBounds(screenX, screenY, width, height);
        this.window.setVisible(true);
        this.window.requestFocus();
    }

    public void hide() {
        this.owner.removeComponentListener(this.ownerListener);
        this.window.setVisible(false);
        this.window.dispose();
    }
    
    public JWindow getWindow() {
        return this.window;
    }
}
