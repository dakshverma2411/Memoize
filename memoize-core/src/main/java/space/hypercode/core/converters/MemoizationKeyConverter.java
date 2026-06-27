package space.hypercode.core.converters;

/**
 * Marker interface for all memoization key converters.
 *
 * <p>Implementations transform method arguments into a {@link String} cache key.
 * Use {@link SingleArgMemoizationKeyConverter} for methods with a single argument,
 * or {@link MultiArgMemoizationKeyConverter} for methods with multiple arguments.
 *
 * <p>Custom converters must have a public no-arg constructor so the framework
 * can instantiate them via reflection.
 *
 * @see SingleArgMemoizationKeyConverter
 * @see MultiArgMemoizationKeyConverter
 */
public interface MemoizationKeyConverter {}
