package space.hypercode.core.models;

/**
 * Interface for objects that can produce their own cache key for memoization.
 *
 * <p>When a method annotated with {@link space.hypercode.core.annotations.MemoizeThis}
 * has a single argument implementing this interface and no explicit converter is specified,
 * the framework automatically uses {@link space.hypercode.core.converters.MemoizableKeyConverter}
 * to delegate key generation to {@link #memoizationKey()}.
 */
public interface Memoizable {
    /**
     * Returns a string key that uniquely identifies this object for cache lookup purposes.
     * Two objects that are logically equivalent for caching should return the same key.
     *
     * @return the cache key representation of this object
     */
    String memoizationKey();
}
