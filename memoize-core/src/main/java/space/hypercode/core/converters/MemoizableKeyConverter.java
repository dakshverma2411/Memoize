package space.hypercode.core.converters;

import space.hypercode.core.models.Memoizable;

/**
 * Internal converter that delegates key generation to {@link Memoizable#memoizationKey()}.
 * Used automatically when a method has a single argument implementing {@link Memoizable}
 * and no explicit converter is specified.
 */
public final class MemoizableKeyConverter implements SingleArgMemoizationKeyConverter<Memoizable> {

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to {@link Memoizable#memoizationKey()}. Returns {@code null} if
     * the input is {@code null}, which causes the framework to skip caching.
     */
    @Override
    public String toKey(final Memoizable input) {
        if (input == null) {
            return null;
        }
        return input.memoizationKey();
    }
}
