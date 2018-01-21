package c.lydiawang.dualpressure;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by lydiawang on 1/18/18.
 */

public class Diamond extends Geom {
    protected int strokeWidth;
    protected int fillColor;
    protected ColorStateList strokeColor;
    protected Path path;
    protected Paint strokePaint;
    protected Paint fillPaint;

    public Diamond(int strokeWidth, int fillColor, ColorStateList strokeColor) {
        super(strokeWidth, fillColor, strokeColor);
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
        path = new Path();
        path.moveTo(width/2, 0);
        path.lineTo(width, height/2);
        path.lineTo(width/2, height);
        path.lineTo(0, height/2);
        path.close();
    }
}
