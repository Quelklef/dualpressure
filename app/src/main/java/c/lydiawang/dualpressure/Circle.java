package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class Circle extends Geom {
    public Circle(Rect bounds, Paint stroke, Paint fill, Paint shineFill) {
        super(bounds, stroke, fill, shineFill);
    }

    @Override
    protected Path makePath() {
        Path path = new Path();
        path.addCircle(bounds.centerX(), bounds.centerY(), Math.min(bounds.width(), bounds.height()) / 2, Path.Direction.CCW);
        return path;
    }
}
