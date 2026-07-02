package space.hypercode.core.providers.threadlocal;

import space.hypercode.core.providers.MemoizationProvider;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadLocalMemoizationProvider extends MemoizationProvider {

    /**
     * key -> value
     */
    private static final ThreadLocal<Map<String, Object>> localContext = new ThreadLocal<>();

    /**
     * Constructs a new provider for a named cache.
     *
     * @param memoizationName the logical name of the cache
     */
    public ThreadLocalMemoizationProvider(final String memoizationName) {
        super(memoizationName, Duration.ZERO, 0L);
    }

    @Override
    public Optional<Object> getValueIfPresent(final String key) {
        return Optional.ofNullable(localContext.get().get(key));
    }

    @Override
    public void put(final String key, final Object value) {
        if(localContext.get() == null) {
            localContext.set(new HashMap<>());
        }
        localContext.get().put(key, value);
    }

    @Override
    public void evictIfPresent(final String key) {
        if(localContext.get() != null) {
            localContext.get().remove(key);
        }
    }
}
