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

public class MainActivity extends AppCompatActivity {

    private float shapeSize;
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


    private Grid<Geom> grid;

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
        shapeSize = getResources().getDimension(R.dimen.shapeSize);

        int triangleFillColor = getResources().getColor(R.color.triangleCol);
        int triangleShineFillColor = getResources().getColor(R.color.triangleColor);
        int squareFillColor = getResources().getColor(R.color.squareCol);
        int squareShineFillColor = getResources().getColor(R.color.squareColor);
        int circleFillColor = getResources().getColor(R.color.circleCol);
        int circleShineFillColor = getResources().getColor(R.color.circleColor);
        int diamondFillColor = getResources().getColor(R.color.diamondCol);
        final int diamondShineFillColor = getResources().getColor(R.color.diamondColor);


        // TODO: Remove
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
        grid.populate();

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        drawingArea = new DrawingArea(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        drawingArea.setLayoutParams(layoutParams);
        assert mainLayout != null;
        mainLayout.addView(drawingArea);
    }

    private class DrawingArea extends View {
        private Paint linePaint = new Paint();
        private Geom swapGeom = null;
        //private Blinker blinker = new Blinker();

        private volatile boolean shine = false;

        private Runnable blinker = new Runnable() {

            @Override
            public void run(){
                try {
                    shine = true;
                    setBlinking(swapGeom, shine);
                    postInvalidate();
                    Thread.sleep(150);

                    shine = false;
                    setBlinking(swapGeom, shine);
                    postInvalidate();
                    Thread.sleep(150);

                    shine = true;
                    setBlinking(swapGeom, shine);
                    postInvalidate();
                    Thread.sleep(150);

                    shine = false;
                    setBlinking(swapGeom, shine);
                    postInvalidate();
                    Thread.sleep(150);

                } catch (InterruptedException e) {
                    shine = false;
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
                if (g.bounds.contains(x, y)) {
                    return g;
                }
            }
            return null;
        }

        private Rect makeBounds(int x, int y, int size) {
            int halfSize = size / 2;
            return new Rect(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
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
                                grid.swap(selected, swapGeom);
                            }
                            swapGeom = null;
                        }
                    }

                    invalidate();
                    return true;
            }

            return super.onTouchEvent(event);
        }

        /**
         * Sets blinking state of all items
         * in grid whose type matches that of g.
         * No-op if g is null.
         */
        private void setBlinking(Geom g, boolean blinking) {
            if (g == null) return;
            Class clazz = g.getClass();
            for (Geom item : grid.getAll()) {
                if (item.getClass().equals(clazz)) {
                    item.shine = blinking;
                }
            }
        }

        private final int padding = 40;
        @Override
        protected void onDraw(Canvas canvas) {

            for (int col = 0; col < grid.width; col++) {
                for (int row = 0; row < grid.height; row++) {
                    Geom g = grid.get(col, row);
                    int ss = (int) shapeSize;
                    int offset = (int) (shapeSize / 2) + padding;
                    g.setBounds(makeBounds((ss + padding) * col + offset + 40, (ss + padding) * row + offset + 20, ss)); // Because they started as boundless
                    g.draw(canvas);
                }
            }
        }
    }
}