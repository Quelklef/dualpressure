package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Array;
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

    public Thread animate(final List<T> shapes, final List<Rect> fromBoundss, final List<Rect> toBoundss, final int sleepTime) {

        if (!(shapes.size() == fromBoundss.size() && fromBoundss.size() == toBoundss.size())) {
            throw new IllegalArgumentException("shapes, fromBoundss, and toBoundss must be same length.");
        }

        final int steps = 20;

        final int[] fromT = new int[shapes.size()];
        final int[] fromR = new int[shapes.size()];
        final int[] fromB = new int[shapes.size()];
        final int[] fromL = new int[shapes.size()];

        final int[] dT = new int[shapes.size()];
        final int[] dR = new int[shapes.size()];
        final int[] dB = new int[shapes.size()];
        final int[] dL = new int[shapes.size()];

        for(int i = 0; i < shapes.size(); i++){
            fromT[i] = fromBoundss.get(i).top;
            fromR[i] = fromBoundss.get(i).right;
            fromB[i] = fromBoundss.get(i).bottom;
            fromL[i] = fromBoundss.get(i).left;

            dT[i] = (toBoundss.get(i).top    - fromT[i]) / steps;
            dR[i] = (toBoundss.get(i).right  - fromR[i]) / steps;
            dB[i] = (toBoundss.get(i).bottom - fromB[i]) / steps;
            dL[i] = (toBoundss.get(i).left   - fromL[i]) / steps;
        }

        new Thread(invalidate).start();

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) { }

                for(int step = 1; step < steps; step++){
                    for (int shape = 0; shape < shapes.size(); shape++) {
                        Rect bounds = new Rect(
                                fromL[shape] + step * dL[shape],
                                fromT[shape] + step * dT[shape],
                                fromR[shape] + step * dR[shape],
                                fromB[shape] + step * dB[shape]
                        );

                        shapes.get(shape).setBounds(bounds);
                        new Thread(invalidate).start();
                    }

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) { }
                }

                for (int shape = 0; shape < shapes.size(); shape++) {
                    // Do final step directly to circumvent floating-point inaccuracies
                    shapes.get(shape).setBounds(toBoundss.get(shape));
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

    /**
     * Takes: A shape, goal final location, and time spend on animation (in ms)
     */
    public Thread animate(final T shape, final Rect fromBounds, final Rect toBounds, int sleepTime) {
        return animate(
                new ArrayList<T>() {{
                    add(shape);
                }},
                new ArrayList<Rect>() {{
                    add(fromBounds);
                }},
                new ArrayList<Rect>() {{
                    add(toBounds);
                }},
                sleepTime
        );
    }

    private static Rect cloneRect(Rect r) {
        return new Rect(r.left, r.top, r.right, r.bottom);
    }

    public Thread animate(T shape, Rect toBounds, int sleepTime) {
        return animate(
                shape,
                cloneRect(shape.getBounds()),
                toBounds,
                sleepTime
        );
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
