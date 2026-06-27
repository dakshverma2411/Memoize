package space.hypercode.core.converters;

/**
 * Key converter for methods with multiple arguments.
 *
 * <p>Converts all method arguments into a single {@link String} cache key.
 *
 * @see SingleArgMemoizationKeyConverter
 */
public interface MultiArgMemoizationKeyConverter extends MemoizationKeyConverter {
    /**
     * Converts the given method arguments to a cache key string.
     *
     * @param args the method arguments (may be {@code null} or empty)
     * @return the cache key, or {@code null} to skip caching for this invocation
     */
    String toKey(Object ... args);
}
