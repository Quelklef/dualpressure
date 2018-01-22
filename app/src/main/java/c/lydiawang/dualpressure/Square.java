package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class Square extends Geom {
    public Square(Rect bounds, Paint stroke, Paint fill) {
        super(bounds, stroke, fill);
    }

    @Override
    protected Path makePath() {
        Path path = new Path();
        path.moveTo(bounds.left, bounds.top);
        path.lineTo(bounds.right, bounds.top);
        path.lineTo(bounds.right, bounds.bottom);
        path.lineTo(bounds.left, bounds.bottom);
        path.close();
        return path;
    }
}
