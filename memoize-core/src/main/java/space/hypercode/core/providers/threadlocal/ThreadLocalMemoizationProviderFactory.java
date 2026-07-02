package space.hypercode.core.providers.threadlocal;

import space.hypercode.core.providers.MemoizationProvider;
import space.hypercode.core.providers.MemoizationProviderFactory;

import java.time.Duration;

public class ThreadLocalMemoizationProviderFactory implements MemoizationProviderFactory {
    @Override
    public MemoizationProvider create(final String memoizationName,
                                      final Duration ttl,
                                      final long maxSize) {
        return new ThreadLocalMemoizationProvider(memoizationName);
    }
}
