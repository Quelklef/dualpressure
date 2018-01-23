package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GridRenderer<T extends Drawable> extends Drawable {

    private Grid<T> grid;
    private int hPadding;
    private int vPadding;
    private float itemSize;

    private Runnable invalidate;

    public GridRenderer(Grid<T> grid, float shapeSize, int hPadding, int vPadding,
                        Runnable invalidate) {
        this.invalidate = invalidate;

        this.grid = grid;
        this.hPadding = hPadding;
        this.vPadding = vPadding;
        this.itemSize = shapeSize;
    }

    private Rect makeBounds(int x, int y, int size) {
        int halfSize = size / 2;
        return new Rect(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
    }

    // --
    // Drawing

    public Rect positonToBounds(int x, int y) {
        int hOffset = (int) (itemSize / 2) + hPadding;
        int vOffset = (int) (itemSize / 2) + vPadding;
        int is = (int) itemSize;
        return makeBounds((is + hPadding) * x + hOffset /*+ 40*/,
                (is + vPadding) * y + vOffset /*+ 20*/, is);
    }

    /**
     * Updates the render bounds of the item to match its position.
     * Should be called when its position updates, unless this will
     * be handled by an animation instead.
     */
    public void updateBounds(T item) {
        item.setBounds(positonToBounds(grid.colOf(item), grid.rowOf(item)));
    }

    /**
     * Update all bounds
     */
    public void refreshBounds() {
        for (T item : grid.getAll()) {
            updateBounds(item);
        }
    }

    // --
    // Animations

    private final List<Thread> runningThreads = new LinkedList<>();

    public void animate(final T shape, final Rect toBounds) {
        final int steps = 20;

        final Rect fromBounds = shape.getBounds();
        final int fromT = fromBounds.top;
        final int fromR = fromBounds.right;
        final int fromB = fromBounds.bottom;
        final int fromL = fromBounds.left;

        final int dT = (toBounds.top    - fromT) / steps;
        final int dR = (toBounds.right  - fromR) / steps;
        final int dB = (toBounds.bottom - fromB) / steps;
        final int dL = (toBounds.left   - fromL) / steps;

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) { }

                for (int s = 1; s < steps; s++) {
                    Rect bounds = new Rect(
                            fromL + s * dL,
                            fromT + s * dT,
                            fromR + s * dR,
                            fromB + s * dB
                    );

                    shape.setBounds(bounds);
                    new Thread(invalidate).start();

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) { }
                }

                shape.setBounds(toBounds); // Circumvent floating-point inaccuracies
                new Thread(invalidate).start();
            }
        };

        // Start thread when possible
        synchronized (runningThreads) {
            runningThreads.add(thread);
            thread.start();
        }

        // Remove thread when it finishes
        new Thread() {
            @Override public void run() {
                try {
                    thread.join();
                } catch (InterruptedException e) { }

                synchronized (runningThreads) {
                    runningThreads.remove(thread);
                }
            }
        }.start();
    }

    /**
     * Start the given thread when all CURRENT* animations finish
     * * subject to slight error
     */
    public void onFinish(final Thread callback) {
        new Thread() {
            @Override
            public void run() {
                List<Thread> runningThreadsClone = new LinkedList<>();

                synchronized (runningThreads) {
                    runningThreadsClone.addAll(runningThreads);
                }

                for (Thread t : runningThreadsClone) {
                    if (t == null) continue;  // May have expired
                    try {
                        t.join();
                    } catch (InterruptedException e) { }
                }

                callback.start();
            }
        }.start();
    }

    // --
    // Drawable methods

    @Override
    public void draw(@NonNull Canvas canvas) {
        for (T item : grid.getAll()) {
            if (item == null) continue;
            item.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
