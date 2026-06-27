package space.hypercode.core.metrics;

/**
 * A no-op implementation of {@link MemoizationMetrics} that silently discards all metrics.
 *
 * <p>Used as the default when no metrics implementation is provided to {@link space.hypercode.core.Memoize}.
 */
public class NoOpMetrics implements MemoizationMetrics {
    @Override
    public void recordHit(String memoizationName) {
        // no op
    }

    @Override
    public void recordMiss(String memoizationName) {
        // no op
    }

    @Override
    public void recordPut(String memoizationName) {
        // no op
    }

    @Override
    public void recordEviction(String memoizationName) {
        // no op
    }

    @Override
    public void recordGetDuration(String memoizationName, long durationNanos) {
        // no op
    }

    @Override
    public void recordPutDuration(String memoizationName, long durationNanos) {
        // no op
    }
}
