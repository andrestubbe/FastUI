package fastui.util;

import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.concurrent.locks.LockSupport;

public class Animator {
    private final Component target;
    private final int targetFps;
    private Runnable onTick;
    private Thread thread;
    private volatile boolean running = false;

    public Animator(final Component target, final int targetFps, final Runnable onTick) {
        this.target = target;
        this.targetFps = targetFps;
        this.onTick = onTick;
    }

    public void start() {
        if (running) return;
        running = true;
        thread = new Thread(() -> {
            long lastTime = System.nanoTime();
            final long nsPerTick = 1000000000L / targetFps;

            while (running) {
                long now = System.nanoTime();
                if (now - lastTime >= nsPerTick) {
                    lastTime = now;
                    
                    // Run logic and repaint on EDT to prevent thread interference
                    SwingUtilities.invokeLater(() -> {
                        if (onTick != null) onTick.run();
                        target.repaint();
                    });
                }
                
                // Use high-precision park for tiny intervals
                LockSupport.parkNanos(100000L); // 0.1ms
            }
        }, "FastUI-Animator");
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    public void stop() {
        running = false;
        if (thread != null) {
            thread.interrupt();
        }
    }
}
