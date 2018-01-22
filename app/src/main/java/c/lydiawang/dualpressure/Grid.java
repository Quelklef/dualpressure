package c.lydiawang.dualpressure;

import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by a bird on 1/21/18.
 *
 * Grid go top to bottom; y=0 is at top!!
 * You remember this! It on AP!
 */

public class Grid<T> {
    private List<List<T>> grid = new LinkedList<>();  // [x][y], [col][row]
    public int width;
    public int height;
    private Function<Double, T> generator;

    /**
     * `generator` should map a (random) float in [0, 1) to
     * a random value of type T.
     */
    public Grid(int w, int h, Function<Double, T> generator) {
        width = w;
        height = h;

        for (int x = 0; x < width; x++) {
            List<T> col =new LinkedList<>();
            for (int y = 0; y < height; y++) {
                col.add(null);
            }
            grid.add(col);
        }

        this.generator = generator;
    }

    public void set(int x, int y, T val) {
        grid.get(x).set(y, val);
    }

    public T gen() {
        return generator.apply(Math.random());
    }

    public T get(int x, int y) {
        return grid.get(x).get(y);
    }


    public void swap(int ax, int ay, int bx, int by) {
        if (ax == bx && ay == by) return;
        T tmp = get(ax, ay);
        set(ax, ay, get(bx, by));
        set(bx, by, tmp);
    }

    public void swap(T a, T b) {
        swap(
                colOf(a), rowOf(a),
                colOf(b), rowOf(b)
        );
    }

    public void remove(int x, int y) {
        set(x, y, null);
    }

    public void remove(T item) {
        remove(colOf(item), rowOf(item));
    }

    public boolean inBounds(int x, int y) {
        return x > 0 && y > 0 && x < width && y < height;
    }

    /**
     * Move an item from one spot to another.
     * If (fx, fy) is out of bounds, instead generate
     * a new item and move it into (tx, ty)
     */
    public void moveg(int fx, int fy, int tx, int ty) {
        T item = inBounds(fx, fy) ? get(fx, fy) : gen();
        set(tx, ty, item);
        if (inBounds(fx, fy)) remove(fx, fy);
    }

    /**
     * Move the item above the given item into its space.
     * To fill the created hole, propagate upwards.
     */
    private void dropReplace(int x, int y) {
        moveg(x, y - 1, x, y);
        if (y != 0) {
            dropReplace(x, y - 1);
        }
    }

    /**
     * Move columns down to fillPaint null spots
     * Each column must have only one contiguous null spot
     */
    public void fall() {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (get(col, row) == null) {
                    dropReplace(col, row);
                }
            }
        }
    }

    /**
     * Get all items in grid
     */
    public List<T> getAll() {
        List<T> ret = new LinkedList<>();
        for (List<T> row : grid) {
            ret.addAll(row);
        }
        return ret;
    }

    /**
     * Populates the entire grid with random items.
     */
    public void populate() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                set(x, y, gen());
            }
        }
    }

    public int colOf(T item) {
        for (int col = 0; col < width; col++) {
            if (grid.get(col).contains(item)) {
                return col;
            }
        }
        return -1;
    }

    public int rowOf(T item) {
        List<T> col = grid.get(colOf(item));
        for (int row = 0; row < height; row++) {
            if (item.equals(col.get(row))) {
                return row;
            }
        }
        return -1;
    }

    public boolean adjacent(T a, T b) {
        int ax = colOf(a);
        int ay = rowOf(a);
        int bx = colOf(b);
        int by = rowOf(b);
        return (ax == bx && Math.abs(ay - by) == 1) ||
                (ay == by && Math.abs(ax - bx) == 1);
    }

    public List<List<T>> cols() {
        List<List<T>> ret = new LinkedList<>();
        for (int col = 0; col < width; col++) {
            List<T> colList = new LinkedList<>();
            for (int row = 0; row < height; row++) {
                colList.add(get(col, row));
            }
            ret.add(colList);
        }
        return ret;
    }

    public List<List<T>> rows() {
        List<List<T>> ret = new LinkedList<>();
        for (int row = 0; row < height; row++) {
            List<T> rowList = new LinkedList<>();
            for (int col = 0; col < width; col++) {
                rowList.add(get(col, row));
            }
            ret.add(rowList);
        }
        return ret;
    }
}
