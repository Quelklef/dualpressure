package c.lydiawang.dualpressure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private float lineWidth;
    private int lineColor;
    private DrawingArea drawingArea;

    private Paint circleStroke;
    private Paint circleFill;
    private Paint circleShineFill;

    private Paint triangleStroke;
    private Paint triangleFill;
    private Paint triangleShineFill;

    private Paint squareStroke;
    private Paint squareFill;
    private Paint squareShineFill;

    private Paint diamondStroke;
    private Paint diamondFill;
    private Paint diamondShineFill;

    private volatile Grid<Geom> grid;
    private GridRenderer<Geom> gridfx;

    private Paint newStrokePaint(float width, int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        return paint;
    }

    private Paint newFillPaint(int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        return paint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        float strokeWidth = getResources().getDimension(R.dimen.strokeWidth);
        float shapeSize = getResources().getDimension(R.dimen.shapeSize);

        int triangleFillColor = getResources().getColor(R.color.triangleCol);
        int triangleShineFillColor = getResources().getColor(R.color.triangleColor);

        int squareFillColor = getResources().getColor(R.color.squareCol);
        int squareShineFillColor = getResources().getColor(R.color.squareColor);

        int circleFillColor = getResources().getColor(R.color.circleCol);
        int circleShineFillColor = getResources().getColor(R.color.circleColor);

        int diamondFillColor = getResources().getColor(R.color.diamondCol);
        int diamondShineFillColor = getResources().getColor(R.color.diamondColor);

        // TODO: Remove?
        lineColor = getResources().getColor(R.color.lineColor);
        lineWidth = getResources().getDimension(R.dimen.lineWidth);

        circleStroke = newStrokePaint(strokeWidth, Color.BLACK);
        circleFill = newFillPaint(circleFillColor);
        circleShineFill = newFillPaint(circleShineFillColor);

        triangleStroke = newStrokePaint(strokeWidth, Color.BLACK);
        triangleFill = newFillPaint(triangleFillColor);
        triangleShineFill = newFillPaint(triangleShineFillColor);

        squareStroke = newStrokePaint(strokeWidth, Color.BLACK);
        squareFill = newFillPaint(squareFillColor);
        squareShineFill = newFillPaint(squareShineFillColor);

        diamondStroke = newStrokePaint(strokeWidth, Color.BLACK);
        diamondFill = newFillPaint(diamondFillColor);
        diamondShineFill = newFillPaint(diamondShineFillColor);

        grid = new Grid<>(6, 9, new Function<Double, Geom>() {
            @Override
            public Geom apply(Double val) {
                Rect boundless = new Rect(0, 0, 0, 0);
                int choice = (int) (val * 4);
                if (choice == 0) {
                    return new Circle(boundless, circleStroke, circleFill, circleShineFill);
                } else if (choice == 1) {
                    return new Triangle(boundless, triangleStroke, triangleFill, triangleShineFill);
                } else if (choice == 2) {
                    return new Square(boundless, squareStroke, squareFill, squareShineFill);
                } else if (choice == 3) {
                    return new Diamond(boundless, diamondStroke, diamondFill, diamondShineFill);
                }
                return null;
            }
        });

        gridfx = new GridRenderer<>(grid, shapeSize, 40, 40, new Runnable() {
            @Override
            public void run() {
                drawingArea.postInvalidate();
                //drawingArea.invalidate();
            }
        });

        grid.populate();
        gridfx.refreshBounds();

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        drawingArea = new DrawingArea(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        drawingArea.setLayoutParams(layoutParams);
        assert mainLayout != null;
        mainLayout.addView(drawingArea);
    }

    private final int BLINK_COUNT = 2;
    private class DrawingArea extends View {
        private Paint linePaint = new Paint();
        private Geom swapGeom = null;

        private volatile boolean shine = false;

        private Runnable blinker = new Runnable() {
            @Override
            public void run(){
                try {
                    for (int i = 0; i < BLINK_COUNT; i++) {
                        shine = true;
                        setBlinking(swapGeom, shine);
                        postInvalidate();
                        Thread.sleep(150);

                        shine = false;
                        setBlinking(swapGeom, shine);
                        postInvalidate();
                        Thread.sleep(150);
                    }
                } catch (InterruptedException e) {
                    setBlinking(swapGeom, false);
                    postInvalidate();
                }
            }
        };

        public DrawingArea(Context context) {
            super(context);
            linePaint.setColor(lineColor);
            linePaint.setStrokeWidth(lineWidth);
            linePaint.setStyle(Paint.Style.STROKE);
        }

        private Geom findGeomAt(int x, int y) {
            for (Geom g : grid.getAll()) {
                if (g == null) continue;
                if (g.getBounds().contains(x, y)) {
                    return g;
                }
            }
            return null;
        }

        private Rect cloneRect(Rect r) {
            return new Rect(r.left, r.top, r.right, r.bottom);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Geom selected = findGeomAt(x, y);

                    if (selected != null) {
                        if (swapGeom == null) {
                            swapGeom = selected;
                            new Thread(blinker).start();
                        } else {
                            if (grid.adjacent(selected, swapGeom)) {
                                Rect selectedTo = cloneRect(swapGeom.getBounds());
                                Rect swapTo = cloneRect(selected.getBounds());

                                grid.swap(selected, swapGeom);

                                gridfx.animate(selected, selectedTo, 10);
                                gridfx.animate(swapGeom, swapTo, 10);
                            }
                            swapGeom = null;
                        }
                    }

                    invalidate();
                    return true;
            }

            gridfx.onFinish(new Thread() {
                @Override
                public void run() {
                    // Continually break groups and fill gaps
                    // until no gaps are left
                    boolean[][] mm;
                    do {
                        mm = matchyMatchy();
                        for (int row = grid.height - 1; row >= 0; row--) {
                            for (int col = 0; col < grid.width; col++) {
                                if (mm[col][row]) {
                                    grid.remove(col, row);

                                    postInvalidate();
                                    try {
                                        Thread.sleep(50);
                                    } catch (InterruptedException e) { }
                                }
                            }
                        }

                        while(grid.hasNulls()){
                            List<Geom> toAnim = new LinkedList<>();
                            List<Rect> origins = new LinkedList<>();
                            List<Rect> destinations = new LinkedList<>();

                            for (int col = 0; col < grid.width; col++) {
                                boolean holeFound = false;

                                for (int row = grid.height - 1; row >= 0; row--) {
                                    Geom item = grid.get(col, row);
                                    if (item == null) holeFound = true;
                                    // Move all down
                                    if (holeFound && item != null) {
                                        toAnim.add(grid.get(col, row));
                                        origins.add(gridfx.positionToBounds(col, row));
                                        destinations.add(gridfx.positionToBounds(col, row + 1));
                                        grid.move(col, row, col, row + 1);
                                    }
                                }

                                // If we found a hole, then we're going to need
                                // to fill in the topmost spot, which will be empty
                                if (holeFound) {
                                    Geom upperItem = grid.moveg(col, -1, col, 0);
                                    //upperItem.setBounds(gridfx.positionToBounds(col, -1));
                                    toAnim.add(upperItem);
                                    origins.add(gridfx.positionToBounds(col, -1));
                                    destinations.add(gridfx.positionToBounds(col, 0));
                                }
                            }

                            Thread animAll = gridfx.animate(
                                    toAnim,
                                    origins,
                                    destinations,
                                    3
                            );

                            try {
                                animAll.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            postInvalidate();
                        }


                        /*
                        // Fill holes
                        for (int col = 0; col < grid.width; col++) {
                            for (int row = grid.height - 1; row >= 0; row--) {
                                if (grid.get(col, row) == null) {

                                    // Find the closest above non-null item
                                    // Initialize to default case (generated item)
                                    Geom upperItem = grid.gen();
                                    upperItem.setBounds(gridfx.positionToBounds(col, -1));
                                    int source = -1; // y-value of found item
                                    for (int roww = row - 1; roww >= 0; roww--) {
                                        Geom item = grid.get(col, roww);
                                        if (item != null) {
                                            upperItem = item;
                                            source = roww;
                                            break;
                                        }
                                    }

                                    // TODO: For some reason, animation not working with generated items
                                    Thread animation = gridfx.animate(upperItem, gridfx.positionToBounds(col, row), 10);
                                    try {
                                        animation.join();
                                    } catch (InterruptedException e) { }

                                    grid.set(col, row, upperItem);
                                    // Remove if wasn't generated
                                    if (source != -1) grid.remove(col, source);
                                }
                            }
                        }
                        */

                    } while (!allFalse(mm));
                }
            });

            return super.onTouchEvent(event);
        }

        /**
         * Sets blinking state of all items
         * in grid whose type matches that of g.
         * No-op if g is null.
         */
        private void setBlinking(Geom g, boolean blinking) {
            if (g == null) return;
            for (Geom item : grid.getAll()) {
                if (matches(g, item)) {
                    item.shine = blinking;
                }
            }
        }

        /**
         * Return if geoms of same shape.
         * False on null a or b.
         */
        private boolean matches(Geom a, Geom b) {
            if (a == null || b == null) return false;
            return a.getClass().equals(b.getClass());
        }

        /**
         * Returns a 2D boolean array where [x][y] is true
         * iff grid[x][y] is part of a contiguous
         */
        private boolean[][] matchyMatchy() {
            boolean[][] arr = new boolean[grid.width][grid.height];

            // Loop over columns
            for (int col = 0; col < grid.width; col++) {
                int groupSize = 1; // group of 1: only head
                Geom head = grid.get(col, 0); // Item at beginning of current group

                for (int row = 1; row < grid.height; row++) {
                    Geom item = grid.get(col, row);
                    if (matches(item, head)) {
                        groupSize++;
                        if (groupSize == 3) {
                            arr[col][row - 2] = true;
                            arr[col][row - 1] = true;
                        }
                        if (groupSize >= 3) {
                            arr[col][row] = true;
                        }
                    } else {
                        head = item;
                        groupSize = 1;
                    }
                }
            }

            // Loop over rows
            for (int row = 0; row < grid.height; row++) {
                int groupSize = 1;
                Geom head = grid.get(0, row);

                for (int col = 1; col < grid.width; col++) {
                    Geom item = grid.get(col, row);
                    if (matches(item, head)) {
                        groupSize++;
                        if (groupSize == 3) {
                            arr[col - 2][row] = true;
                            arr[col - 1][row] = true;
                        }
                        if (groupSize >= 3) {
                            arr[col][row] = true;
                        }
                    } else {
                        head = item;
                        groupSize = 1;
                    }
                }
            }

            return arr;
        }

        private boolean allFalse(boolean[][] ar) {
            for (boolean[] row : ar) {
                for (boolean item : row) {
                    if (item == true) return false;
                }
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            gridfx.draw(canvas);
        }
    }
}