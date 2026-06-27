---
layout: default
title: Providers
nav_order: 5
has_children: true
---

# Providers

Providers are the cache backends that store and retrieve memoized values. Memoize uses a pluggable provider architecture so you can swap cache implementations without changing your application code.

## Architecture

The provider system has two interfaces:

### MemoizationProviderFactory

A factory that creates `MemoizationProvider` instances. One provider instance is created per named cache. You register a single factory at startup.

```java
public interface MemoizationProviderFactory {
    MemoizationProvider create(String memoizationName, Duration ttl, long maxSize);
}
```

### MemoizationProvider

The actual cache implementation. Each named cache gets its own provider instance with its own TTL and size configuration.

```java
public abstract class MemoizationProvider {

    public MemoizationProvider(String memoizationName, Duration ttl, long maxSize);

    // Core operations
    public abstract Optional<Object> getValueIfPresent(String key);
    public abstract void put(String key, Object value);
    public abstract void evictIfPresent(String key);

    // Accessors
    public String getMemoizationName();
    public Duration getTtl();
    public long getMaxSize();
}
```

## Available Providers

| Provider | Status | Description |
|---|---|---|
| [Caffeine]({% link providers/caffeine.md %}) | Available | High-performance in-memory cache using Caffeine |
| [Redis]({% link providers/redis.md %}) | Planned | Distributed caching with Redis |
| [Custom]({% link providers/custom.md %}) | Guide | Build your own provider |

## Provider Lifecycle

1. At startup, you register a `MemoizationProviderFactory` with `Memoize.create().providerFactory(...)`.
2. When a `@MemoizeThis` method is first called, the factory creates a `MemoizationProvider` for that cache name.
3. The provider instance is cached and reused for all subsequent calls to methods with the same cache name.
4. The factory receives the TTL and max size from the annotation (or from `MemoizationConfig` if `useConfig=true`).
