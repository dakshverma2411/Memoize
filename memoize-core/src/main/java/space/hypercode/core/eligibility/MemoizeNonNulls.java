package space.hypercode.core.eligibility;

import space.hypercode.core.models.MemoizeCallContext;

/**
 * An {@link EligibilityCriteria} that only permits caching when the return value is non-null.
 *
 * <p>This is a singleton; obtain the instance via {@link #getInstance()} or
 * {@link EligibilityCriterias#ONLY_NON_NULLS}.
 */
public final class MemoizeNonNulls implements EligibilityCriteria {

    private static final MemoizeNonNulls INSTANCE = new MemoizeNonNulls();

    /**
     * Returns the singleton instance.
     *
     * @return the shared {@link MemoizeNonNulls} instance
     */
    public static MemoizeNonNulls getInstance() {
        return INSTANCE;
    }

    private MemoizeNonNulls() {
        // to avoid instantiation
    }

    /**
     * Returns {@code true} only when the return value in the given context is non-null.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean shouldMemoize(final MemoizeCallContext context) {
        return context.getReturnValue() != null;
    }
}
