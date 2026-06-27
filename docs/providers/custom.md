---
layout: default
title: Custom Provider
parent: Providers
nav_order: 3
---

# Custom Provider

You can implement your own cache backend by implementing two interfaces: `MemoizationProviderFactory` and `MemoizationProvider`.

## Step 1: Implement MemoizationProvider

Extend the abstract `MemoizationProvider` class and implement the three core operations:

```java
import space.hypercode.core.providers.MemoizationProvider;

import java.time.Duration;
import java.util.Optional;

public class MyCustomProvider extends MemoizationProvider {

    public MyCustomProvider(String memoizationName, Duration ttl, long maxSize) {
        super(memoizationName, ttl, maxSize);
        // Initialize your cache backend here
    }

    @Override
    public Optional<Object> getValueIfPresent(String key) {
        // Look up the key in your cache.
        // Return Optional.of(value) on hit, Optional.empty() on miss.
        Object value = myCache.get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public void put(String key, Object value) {
        // Store the key-value pair in your cache.
        // Apply TTL and size limits as appropriate for your backend.
        myCache.put(key, value);
    }

    @Override
    public void evictIfPresent(String key) {
        // Remove the key from your cache if it exists.
        myCache.remove(key);
    }
}
```

### Methods to Implement

| Method | Description |
|---|---|
| `getValueIfPresent(String key)` | Returns the cached value wrapped in `Optional`, or `Optional.empty()` if not found. |
| `put(String key, Object value)` | Stores a value in the cache. The value is never `null` unless `MemoizeAlways` criteria is used. |
| `evictIfPresent(String key)` | Removes the entry for the given key, if present. |

### Available from Base Class

| Method | Description |
|---|---|
| `getMemoizationName()` | The name of this cache (from `@MemoizeThis(name=...)` or auto-generated). |
| `getTtl()` | The time-to-live `Duration` for entries in this cache. |
| `getMaxSize()` | The maximum number of entries for this cache. |

## Step 2: Implement MemoizationProviderFactory

Create a factory that produces your custom provider instances:

```java
import space.hypercode.core.providers.MemoizationProviderFactory;
import space.hypercode.core.providers.MemoizationProvider;

import java.time.Duration;

public class MyCustomProviderFactory implements MemoizationProviderFactory {

    @Override
    public MemoizationProvider create(String memoizationName, Duration ttl, long maxSize) {
        return new MyCustomProvider(memoizationName, ttl, maxSize);
    }
}
```

The factory is called once per unique cache name. The resulting provider is cached and reused for all subsequent calls.

## Step 3: Register at Startup

Pass your factory to the `Memoize` builder:

```java
Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new MyCustomProviderFactory())
    .build()
    .start();
```

## Implementation Guidelines

- **Thread safety**: Your provider will be called from multiple threads concurrently. Ensure all cache operations are thread-safe.
- **Null values**: Be aware that `put` may be called with a `null` value if `MemoizeAlways` criteria is used and the method returns `null`. Handle or reject this case gracefully.
- **Fail-safe**: The `MemoizeAspect` catches and logs exceptions from provider operations, so a provider failure will not crash the application. However, throwing exceptions will cause caching to be skipped for that call.
- **TTL and size**: The `ttl` and `maxSize` are passed to your factory. It is your responsibility to enforce them (or ignore them if your backend handles this differently).

## Example: HashMap-Based Provider

A minimal in-memory provider (not production-grade -- no TTL, no size limits, basic synchronization):

```java
import space.hypercode.core.providers.MemoizationProvider;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleMapProvider extends MemoizationProvider {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public SimpleMapProvider(String memoizationName, Duration ttl, long maxSize) {
        super(memoizationName, ttl, maxSize);
    }

    @Override
    public Optional<Object> getValueIfPresent(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public void put(String key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void evictIfPresent(String key) {
        cache.remove(key);
    }
}

public class SimpleMapProviderFactory implements MemoizationProviderFactory {
    @Override
    public MemoizationProvider create(String memoizationName, Duration ttl, long maxSize) {
        return new SimpleMapProvider(memoizationName, ttl, maxSize);
    }
}
```
