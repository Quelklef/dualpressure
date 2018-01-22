package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Path;

public class Diamond extends Geom {
    public Diamond(Rect bounds, Paint stroke, Paint fill) {
        super(bounds, stroke, fill);
    }

    protected Path makePath() {
        Path path = new Path();
        path.moveTo(bounds.centerX(), bounds.top);
        path.lineTo(bounds.right, bounds.centerY());
        path.lineTo(bounds.centerX(), bounds.bottom);
        path.lineTo(bounds.left, bounds.centerY());
        path.close();
        return path;
    }
}
