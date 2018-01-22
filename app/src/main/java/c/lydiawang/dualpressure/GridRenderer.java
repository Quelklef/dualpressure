package c.lydiawang.dualpressure;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GridRenderer<T extends Drawable> extends Drawable {

    private Grid<T> grid;
    private int hPadding;
    private int vPadding;
    private float itemSize;

    public GridRenderer(Grid<T> grid, float shapeSize, int hPadding, int vPadding) {
        this.grid = grid;
        this.hPadding = hPadding;
        this.vPadding = vPadding;
        this.itemSize = shapeSize;
    }

    private Rect makeBounds(int x, int y, int size) {
        int halfSize = size / 2;
        return new Rect(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
    }

    private void updateBounds(T item) {
        int hOffset = (int) (itemSize / 2) + hPadding;
        int vOffset = (int) (itemSize / 2) + vPadding;
        int is = (int) itemSize;
        item.setBounds(makeBounds((is + hPadding) * grid.colOf(item) + hOffset /*+ 40*/,
                (is + vPadding) * grid.rowOf(item) + vOffset /*+ 20*/, is));
    }

    // --
    // Drawable methods

    @Override
    public void draw(@NonNull Canvas canvas) {
        for (T item : grid.getAll()) {
            updateBounds(item); // TODO Doesn't actually need to update each tick..
            item.draw(canvas);
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
