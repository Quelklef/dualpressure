package c.lydiawang.dualpressure;

/**
 * Created by a bird on 1/21/18.
 *
 * Grid go top to bottom; y=0 is at top!!
 * You remember this! It on AP!
 */

public class Grid<T> {
    private T[][] grid;  // [x][y], [col][row]
    private int width;
    private int height;
    private Function<Double, T> generator;

    /**
     * `generator` should map a (random) float in [0, 1) to
     * a random value of type T.
     */
    public Grid(int w, int h, Function<Double, T> generator) {
        width = w;
        height = h;
        grid = (T[][]) new Object[w][h];
        this.generator = generator;
    }

    public void set(int x, int y, T val) {
        grid[x][y] = val;
    }

    public T gen() {
        return generator.apply(Math.random());
    }

    public T get(int x, int y) {
        return grid[x][y];
    }

    /**
     * get-forgiving. Returns get(x, y) or null
     */
    public T getf(int x, int y) {
        return inBounds(x, y) ? get(x, y) : null;
    }

    public void swap(int ax, int ay, int bx, int by) {
        T tmp = grid[ax][ay];
        grid[ax][ay] = grid[bx][by];
        grid[bx][by] = tmp;
    }

    public void remove(int x, int y) {
        grid[x][y] = null;
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
    }

    /**
     * Move columns down to fill null spots
     * Each column must have only one contiguous null spot
     */
    public void fall() {
        for (int col = 0; col < width; col++) {
            int holeStop = -1; // Index of bottom-most null in hole
            int holeSize = 0;
            boolean holeExists = false;

            for (int row = 0; row < height; row++) {
                if (get(col, row) == null) {
                    holeExists = true;
                    holeSize++;
                    holeStop = row;
                }
            }

            // Skip row if there's no hole
            if (!holeExists) continue;

            // Fill in the hole
            for (int row = holeStop; row <= 0; row--) {
                moveg(col, row, col, row - holeSize);
            }
        }
    }
}
