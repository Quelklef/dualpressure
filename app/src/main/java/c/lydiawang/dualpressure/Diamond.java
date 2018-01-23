package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Path;

public class Diamond extends Geom {
    public Diamond(Rect bounds, Paint stroke, Paint fill, Paint shineFill) {
        super(bounds, stroke, fill, shineFill);
    }

    protected Path makePath() {
        Path path = new Path();
        path.moveTo(getBounds().centerX(), getBounds().top);
        path.lineTo(getBounds().right, getBounds().centerY());
        path.lineTo(getBounds().centerX(), getBounds().bottom);
        path.lineTo(getBounds().left, getBounds().centerY());
        path.close();
        return path;
    }
}
