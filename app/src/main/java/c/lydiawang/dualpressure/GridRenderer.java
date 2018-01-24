package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    public Rect positionToBounds(int x, int y) {
        int hOffset = (int) (itemSize / 2) + hPadding;
        int vOffset = (int) (itemSize / 2) + vPadding;
        int is = (int) itemSize;
        return makeBounds(
                (is + hPadding) * x + hOffset /*+ 40*/,
                (is + vPadding) * y + vOffset /*+ 20*/,
                is
        );
    }

    /**
     * Updates the render bounds of the item to match its position.
     * Should be called when its position updates, unless this will
     * be handled by an animation instead.
     */
    public void updateBounds(T item) {
        item.setBounds(positionToBounds(grid.colOf(item), grid.rowOf(item)));
    }

    /**
     * Update all bounds
     */
    public void refreshBounds() {
        for (T item : grid.getAll()) {
            if (item == null) continue;
            updateBounds(item);
        }
    }

    // --
    // Animations

    private final List<Thread> runningThreads = new LinkedList<>();

    /**
     * Takes: A shape, goal final location, and time spend on animation (in ms)
     */
    public Thread animate(final T shape, final Rect toBounds, final int steps, final int sleepTime) {
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
                    Thread.sleep(sleepTime);
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
                        Thread.sleep(sleepTime);
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

        return thread;
    }

    /**
     * Takes: A shape, goal final location, and time spend on animation (in ms)
     */
    public Thread animateAll(final T[] shape, final Rect[] fromBounds, final Rect[] toBounds, final int steps, final int sleepTime) {
        final int[] fromT = new int[shape.length];
        final int[] fromR = new int[shape.length];
        final int[] fromB = new int[shape.length];
        final int[] fromL = new int[shape.length];

        final int[] dT = new int[shape.length];
        final int[] dR = new int[shape.length];
        final int[] dB = new int[shape.length];
        final int[] dL = new int[shape.length];

        for(int i = 0; i < shape.length; i++){
		    T shap = shape[i];
		    Geom g = (Geom) shap;
		    g.shine = true;
		    fromBounds[i] = shap.getBounds();
            fromT[i] = fromBounds[i].top;
            fromR[i] = fromBounds[i].right;
            fromB[i] = fromBounds[i].bottom;
            fromL[i] = fromBounds[i].left;

            dT[i] = (toBounds[i].top    - fromT[i]) / steps;
            dR[i] = (toBounds[i].right  - fromR[i]) / steps;
            dB[i] = (toBounds[i].bottom - fromB[i]) / steps;
            dL[i] = (toBounds[i].left   - fromL[i]) / steps;
        }

        new Thread(invalidate).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) { }

                for(int i = 0; i < shape.length; i++){
                    for (int s = 1; s < steps; s++) {
                        Rect bounds = new Rect(
                                fromL[i] + s * dL[i],
                                fromT[i] + s * dT[i],
                                fromR[i] + s * dR[i],
                                fromB[i] + s * dB[i]
                        );

                        shape[i].setBounds(bounds);
                        new Thread(invalidate).start();

                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) { }
                    }
                    shape[i].setBounds(toBounds[i]); // Circumvent floating-point inaccuracies
                }

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

        return thread;
    }

    public Thread animateTimed(T shape, Rect toBounds, int time) {
        return animate(shape, toBounds, 5, time / 20);
    }

    public Thread animateTimedAll(T[] shape, Rect[] fromBounds, Rect[] toBounds, int time) {
        return animateAll(shape, fromBounds, toBounds, 5, time / 20);
    }

    public Thread animateTimed(T shape, Rect toBounds) {
        // Default time: 1s
        return animateTimed(shape, toBounds, 100);
    }

    /**
     * Takes: A shape, goal final location, and animation speed (in px/ms)
     */
    // TODO: Would like to use for falling items but is SUPER broken
    public Thread animateClocked(T shape, Rect toBounds, int slowness) {
        Rect fromBounds = shape.getBounds();
        double distance = Math.sqrt(
                Math.pow(toBounds.centerX() - fromBounds.centerX(), 2)
                + Math.pow(toBounds.centerY() - fromBounds.centerY(), 2)
        );

        return animate(shape, toBounds, (int) distance / 20, slowness);
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
