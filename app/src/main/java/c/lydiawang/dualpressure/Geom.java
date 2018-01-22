package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.Nullable;

public abstract class Geom extends android.graphics.drawable.Drawable {
    protected Rect bounds;
    protected Paint stroke;
    protected Paint fill;

    protected Path path;

    public Geom(Rect bounds, Paint stroke, Paint fill) {
        this.bounds = bounds;
        this.stroke = stroke;
        this.fill = fill;
    }

    protected abstract Path makePath();

    @Override
    public void draw(Canvas canvas) {
        path = makePath();
        canvas.drawPath(path, stroke);
        canvas.drawPath(path, fill);
    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
