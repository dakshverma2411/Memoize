package space.hypercode.core.converters;

/**
 * Key converter for methods with a single argument.
 *
 * <p>Converts the single method argument of type {@code T} into a {@link String} cache key.
 *
 * @param <T> the type of the method argument
 * @see MultiArgMemoizationKeyConverter
 */
public interface SingleArgMemoizationKeyConverter<T> extends MemoizationKeyConverter {
    /**
     * Converts the given argument to a cache key string.
     *
     * @param input the method argument (may be {@code null})
     * @return the cache key, or {@code null} to skip caching for this invocation
     */
    String toKey(T input);
}