package space.hypercode.core.models;

import lombok.Builder;
import lombok.Value;

/**
 * Immutable context object representing a memoized method invocation and its result.
 *
 * <p>Passed to {@link space.hypercode.core.eligibility.EligibilityCriteria#shouldMemoize(MemoizeCallContext)}
 * to determine whether the return value should be cached.
 */
@Value
@Builder
public class MemoizeCallContext {
    /** The value returned by the original method invocation. May be {@code null}. */
    Object returnValue;
    /** The arguments passed to the original method invocation. */
    Object[] args;
}
