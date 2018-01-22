package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class Geom extends android.graphics.drawable.Drawable {
    protected Rect bounds;

    public Paint strokePaint;
    public Paint fillPaint;

    public boolean shine; // make it shiny!
    private static Paint shineStrokePaint;
    private static Paint shineFillPaint;
    static {
        shineStrokePaint = new Paint();
        shineStrokePaint.setStyle(Paint.Style.STROKE);
        shineStrokePaint.setColor(Color.YELLOW);

        shineFillPaint = new Paint();
        shineFillPaint.setStyle(Paint.Style.FILL);
        shineFillPaint.setColor(Color.BLACK);
    }

    protected Path path;

    public Geom(Rect bounds, Paint stroke, Paint fill) {
        this.bounds = bounds;
        this.strokePaint = stroke;
        this.fillPaint = fill;
    }

    protected abstract Path makePath();

    @Override
    public void setBounds(@NonNull Rect bounds) {
        this.bounds = bounds;
    }
    // DON'T CALL .getBounds() !!

    @Override
    public void draw(@NonNull Canvas canvas) {
        path = makePath();
        if (shine) {
            canvas.drawPath(path, shineStrokePaint);
            canvas.drawPath(path, shineFillPaint);
        } else {
            canvas.drawPath(path, strokePaint);
            canvas.drawPath(path, fillPaint);
        }
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
