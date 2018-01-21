package c.lydiawang.dualpressure;

/**
 * Because using builtin Function requires API 24...
 */

public interface Function<F, T> {
    T apply(F val);
}
