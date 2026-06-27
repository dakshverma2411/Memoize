package space.hypercode.providers.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import space.hypercode.core.providers.MemoizationProvider;

import java.time.Duration;
import java.util.Optional;

/**
 * {@link MemoizationProvider} implementation backed by a Caffeine {@link Cache}.
 *
 * <p>Supports optional TTL-based expiration and maximum-size eviction.
 * When TTL is {@code null}, zero, or negative, entries never expire.
 * When {@code maxSize} is {@code <= 0}, no size-based eviction is applied.
 */
public class CaffeineMemoizationProvider extends MemoizationProvider {

    private final Cache<String, Object> cache;

    /**
     * Creates a new Caffeine-backed memoization provider.
     *
     * @param memoizationName the logical name of the cache
     * @param ttl             time-to-live for entries; {@code null}, zero, or negative disables expiration
     * @param maxSize         maximum number of entries; values {@code <= 0} disable size-based eviction
     */
    public CaffeineMemoizationProvider(final String memoizationName,
                                       final Duration ttl,
                                       final long maxSize) {
        super(memoizationName, ttl, maxSize);

        final Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (ttl != null && !ttl.isZero() && !ttl.isNegative()) {
            builder.expireAfterWrite(ttl);
        }

        if (maxSize > 0) {
            builder.maximumSize(maxSize);
        }

        this.cache = builder.build();
    }

    /**
     * Returns the cached value associated with {@code key}, if present.
     *
     * @param key the cache key to look up
     * @return an {@link Optional} containing the cached value, or empty if absent
     */
    @Override
    public Optional<Object> getValueIfPresent(final String key) {
        return Optional.ofNullable(cache.getIfPresent(key));
    }

    /**
     * Stores a value in the cache under the given key.
     *
     * @param key   the cache key
     * @param value the value to cache
     */
    @Override
    public void put(final String key, final Object value) {
        cache.put(key, value);
    }

    /**
     * Evicts the entry associated with the given key, if present.
     *
     * @param key the cache key to invalidate
     */
    @Override
    public void evictIfPresent(final String key) {
        cache.invalidate(key);
    }
}
