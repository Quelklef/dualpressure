package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class Geom extends android.graphics.drawable.Drawable {
    public Paint strokePaint;
    public Paint fillPaint;

    public boolean shine; // make it shiny!
    private Paint shineFillPaint;

    protected Path path;

    public Geom(Rect bounds, Paint stroke, Paint fill, Paint shineFill) {
        setBounds(bounds);
        this.strokePaint = stroke;
        this.fillPaint = fill;
        this.shineFillPaint = shineFill;
    }

    protected abstract Path makePath();

    @Override
    public void draw(@NonNull Canvas canvas) {
        path = makePath();
        if (shine) {
            canvas.drawPath(path, strokePaint);
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
