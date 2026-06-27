package space.hypercode.core.eligibility;

import space.hypercode.core.models.MemoizeCallContext;

/**
 * An {@link EligibilityCriteria} that always permits caching, regardless of the return value.
 *
 * <p>This is the default criteria used when none is explicitly specified.
 * It is a singleton; obtain the instance via {@link #getInstance()}.
 */
public final class MemoizeAlways implements EligibilityCriteria {

    private static final MemoizeAlways INSTANCE = new MemoizeAlways();

    /**
     * Returns the singleton instance.
     *
     * @return the shared {@link MemoizeAlways} instance
     */
    public static MemoizeAlways getInstance() {
        return INSTANCE;
    }

    private MemoizeAlways() {
        // to avoid instantiation
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldMemoize(final MemoizeCallContext context) {
        return true;
    }
}
