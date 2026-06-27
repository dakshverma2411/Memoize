package space.hypercode.memoize.examples;

import space.hypercode.core.converters.SingleArgMemoizationKeyConverter;

/**
 * Example {@link SingleArgMemoizationKeyConverter} that converts a {@link Long} argument
 * to its string representation for use as a cache key.
 */
public class LongConverter implements SingleArgMemoizationKeyConverter<Long> {
    /**
     * Converts the given {@link Long} to its string representation.
     *
     * @param input the value to convert
     * @return the decimal string form of {@code input}
     */
    @Override
    public String toKey(Long input) {
        return input.toString();
    }
}
