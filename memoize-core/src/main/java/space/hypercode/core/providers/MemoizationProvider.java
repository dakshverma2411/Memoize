package space.hypercode.core.providers;

import java.time.Duration;
import java.util.Optional;

/**
 * Abstract base class for cache provider implementations.
 *
 * <p>Each instance manages a single named cache with a configured TTL and maximum size.
 * Subclasses must implement the actual cache storage operations:
 * {@link #getValueIfPresent(String)}, {@link #put(String, Object)}, and
 * {@link #evictIfPresent(String)}.
 *
 * <p>Instances are created by a {@link MemoizationProviderFactory} and are
 * cached per memoization name by the framework.
 */
public abstract class MemoizationProvider {

    private final String memoizationName;
    private final Duration ttl;
    private final long maxSize;

    /**
     * Constructs a new provider for a named cache.
     *
     * @param memoizationName the logical name of the cache
     * @param ttl             the time-to-live for entries in this cache
     * @param maxSize         the maximum number of entries (0 for unlimited)
     */
    public MemoizationProvider(
            final String memoizationName,
            final Duration ttl,
            final long maxSize) {
       this.memoizationName = memoizationName;
       this.ttl = ttl;
       this.maxSize = maxSize;
    }

    /**
     * Returns the logical name of this cache.
     *
     * @return the memoization name
     */
    public String getMemoizationName() {
        return memoizationName;
    }

    /**
     * Returns the time-to-live for entries in this cache.
     *
     * @return the TTL duration
     */
    public Duration getTtl() {
        return ttl;
    }

    /**
     * Returns the maximum number of entries allowed in this cache.
     *
     * @return the max size, or 0 if unlimited
     */
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * Retrieves a cached value by key.
     *
     * @param key the cache key
     * @return an {@link Optional} containing the cached value, or empty if not present
     */
    public abstract Optional<Object> getValueIfPresent(final String key);

    /**
     * Stores a value in the cache.
     *
     * @param key   the cache key
     * @param value the value to cache
     */
    public abstract void put(final String key, final Object value);

    /**
     * Evicts an entry from the cache if it is present.
     *
     * @param key the cache key to evict
     */
    public abstract void evictIfPresent(final String key);
}
