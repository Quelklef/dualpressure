package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class Triangle extends Geom {
    public Triangle(Rect bounds, Paint stroke, Paint fill) {
        super(bounds, stroke, fill);
    }

    @Override
    protected Path makePath() {
        Path path = new Path();
        path.moveTo(bounds.centerX(), bounds.top);
        path.lineTo(bounds.right, bounds.bottom);
        path.lineTo(bounds.left, bounds.bottom);
        path.close();
        return path;
    }
}
