package c.lydiawang.dualpressure;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

public class Square extends Geom {
    public Square(Rect bounds, Paint stroke, Paint fill, Paint shineFill) {
        super(bounds, stroke, fill, shineFill);
    }

    @Override
    protected Path makePath() {
        Path path = new Path();
        path.moveTo(getBounds().left, getBounds().top);
        path.lineTo(getBounds().right, getBounds().top);
        path.lineTo(getBounds().right, getBounds().bottom);
        path.lineTo(getBounds().left, getBounds().bottom);
        path.close();
        return path;
    }
}
