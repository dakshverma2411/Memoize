package space.hypercode.core.eligibility;

import space.hypercode.core.models.MemoizeCallContext;

/**
 * Determines whether a method's return value is eligible for caching.
 *
 * <p>Implementations are used by the memoization framework after the original method
 * executes to decide if the result should be stored in the cache. Built-in
 * implementations are available via {@link EligibilityCriterias}.
 *
 * @see MemoizeAlways
 * @see MemoizeNonNulls
 */
public interface EligibilityCriteria {
    /**
     * Evaluates whether the method result should be memoized.
     *
     * @param context the call context containing the return value and method arguments
     * @return {@code true} if the result should be cached, {@code false} to skip caching
     */
    boolean shouldMemoize(final MemoizeCallContext context);
}
