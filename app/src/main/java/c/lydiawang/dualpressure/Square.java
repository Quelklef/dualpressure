package c.lydiawang.dualpressure;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.shapes.Shape;

/**
 * Created by lydiawang on 1/18/18.
 */

public class Square extends Geom {

    public Square(int strokeWidth, int fillColor, ColorStateList strokeColor) {
        super(strokeWidth, fillColor, strokeColor);
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
        path = new Path();
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();
    }
}
