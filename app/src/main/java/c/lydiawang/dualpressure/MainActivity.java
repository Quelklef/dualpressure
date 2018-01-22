package c.lydiawang.dualpressure;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private float shapeSize;
    private float lineWidth;
    private int lineColor;
    private DrawingArea drawingArea;

    private Paint circleStroke;
    private Paint circleFill;

    private Paint triangleStroke;
    private Paint triangleFill;

    private Paint squareStroke;
    private Paint squareFill;

    private enum Mode {
        AddSquare, AddCircle, AddTriangle, Select;
    }
    private Mode mode = null;
    /*private volatile boolean blink = false;

    private Runnable blinker = new Runnable() {
        @Override
        public void run() {
            try {
                blink = true;
                drawingArea.postInvalidate();
                Thread.sleep(250);
                blink = false;
                drawingArea.postInvalidate();
                Thread.sleep(250);
                blink = true;
                drawingArea.postInvalidate();
                Thread.sleep(250);
                blink = false;
                drawingArea.postInvalidate();
                tappedThing = null;
            } catch (InterruptedException e) {
                blink = false;
            }
        }
    };*/

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

        ImageView squareImageView = findViewById(R.id.squareButton);
        ImageView circleImageView = findViewById(R.id.circleButton);
        ImageView triangleImageView = findViewById(R.id.triangleButton);
        ImageView selectionImageView = findViewById(R.id.selectionButton);

        float strokeWidth = getResources().getDimension(R.dimen.strokeWidth);
        shapeSize = getResources().getDimension(R.dimen.shapeSize);

        int triangleFillColor = getResources().getColor(R.color.triangleColor);
        int squareFillColor = getResources().getColor(R.color.squareColor);
        int circleFillColor = getResources().getColor(R.color.circleColor);

        // Hmm
        ColorStateList strokeColor = getResources().getColorStateList(R.color.stroke);

        lineColor = getResources().getColor(R.color.lineColor);
        lineWidth = getResources().getDimension(R.dimen.lineWidth);

        assert squareImageView != null;
        assert circleImageView != null;
        assert triangleImageView != null;

        circleStroke = newStrokePaint(strokeWidth, Color.BLACK);
        circleFill = newFillPaint(circleFillColor);

        triangleStroke = newStrokePaint(strokeWidth, Color.BLACK);
        triangleFill = newFillPaint(triangleFillColor);

        squareStroke = newStrokePaint(strokeWidth, Color.BLACK);
        squareFill = newFillPaint(squareFillColor);

        Rect bounds = new Rect(0, 0, (int) shapeSize, (int) shapeSize);
        circleImageView.setImageDrawable(new Circle(bounds, circleStroke, circleFill));
        triangleImageView.setImageDrawable(new Triangle(bounds, triangleStroke, triangleFill));
        squareImageView.setImageDrawable(new Square(bounds, squareStroke, squareFill));

        selectionImageView.setImageDrawable(new Circle(bounds, circleStroke, circleFill));

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        drawingArea = new DrawingArea(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
        drawingArea.setLayoutParams(layoutParams);
        assert mainLayout != null;
        mainLayout.addView(drawingArea);
    }

    public void buttonPressed(View view) {
        switch(view.getId()) {
            case R.id.circleButton:
                mode = Mode.AddCircle;
                break;
            case R.id.squareButton:
                mode = Mode.AddSquare;
                break;
            case R.id.triangleButton:
                mode = Mode.AddTriangle;
                break;
            case R.id.selectionButton:
                mode = Mode.Select;
                break;
        }
        ViewGroup group = (ViewGroup) view.getParent();
        for(int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child != view) {
                child.setSelected(false);
            }
        }
        view.setSelected(true);
    }

    private class DrawingArea extends View {
        private List<Geom> geoms = new ArrayList<>();
        private Geom selectedThing = null;
        private Paint linePaint = new Paint();

        public DrawingArea(Context context) {
            super(context);
            linePaint.setColor(lineColor);
            linePaint.setStrokeWidth(lineWidth);
            linePaint.setStyle(Paint.Style.STROKE);
        }

        private Geom findGeomAt(int x, int y) {
            for (Geom g : geoms) {
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
            int size = (int) shapeSize;

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    switch(mode) {
                        case AddSquare:
                            geoms.add(new Square(makeBounds(x, y, size), squareStroke, squareFill));
                            break;
                        case AddCircle:
                            geoms.add(new Circle(makeBounds(x, y, size), circleStroke, circleFill));
                            break;
                        case AddTriangle:
                            geoms.add(new Triangle(makeBounds(x, y, size), triangleStroke, triangleFill));
                            break;
                        case Select:
                            selectedThing = findGeomAt(x, y);
                            if (selectedThing != null) {
                                /*tappedThing = selectedThing;
                                things.remove(selectedThing);
                                things.add(selectedThing);
                                new Thread(blinker).start();*/
                            }
                            break;
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (selectedThing != null) {
                        selectedThing.setBounds(makeBounds(x, y, size));
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                    selectedThing = null;
                    invalidate();
                    return true;
            }
            return super.onTouchEvent(event);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            for (Geom g : geoms) {
                g.draw(canvas);
            }
        }
    }

    // ??
    private static final int[] selectedState = {android.R.attr.state_selected};
    private static final int[] unselectedState = {};
}