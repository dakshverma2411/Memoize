---
layout: default
title: Caffeine
parent: Providers
nav_order: 1
---

# Caffeine Provider

The default cache backend, backed by [Caffeine](https://github.com/ben-manes/caffeine) -- a high-performance, near-optimal caching library for Java.

## Dependency

```xml
<dependency>
    <groupId>space.hypercode</groupId>
    <artifactId>memoize-caffeine</artifactId>
</dependency>
```

## Usage

```java
import space.hypercode.providers.caffeine.CaffeineMemoizationProviderFactory;

Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .build()
    .start();
```

## How It Works

`CaffeineMemoizationProviderFactory` creates a `CaffeineMemoizationProvider` for each named cache. Each provider manages its own `Caffeine<String, Object>` cache instance configured with the TTL and max size from the annotation.

### Cache Configuration

The Caffeine cache is configured based on the annotation attributes:

- **TTL** (`ttlInMs`): If greater than 0, sets `expireAfterWrite` on the Caffeine builder. A value of `0` means no time-based expiration.
- **Max size** (`size`): If greater than 0, sets `maximumSize` on the Caffeine builder. A value of `0` means unbounded.

```java
// Cache with 60-second TTL and max 500 entries
@MemoizeThis(ttlInMs = 60000, size = 500)
public User findUser(UserId id) { ... }

// Cache with no expiration, max 100 entries
@MemoizeThis(size = 100)
public Config loadConfig(ConfigKey key) { ... }

// Cache with 5-second TTL, unbounded size
@MemoizeThis(ttlInMs = 5000)
public Price getPrice(ProductId id) { ... }
```

### Eviction Policy

Caffeine uses a **Window TinyLFU** eviction policy, which provides near-optimal hit rates. When the cache reaches `maxSize`, the least valuable entries (based on frequency and recency) are evicted.

### Null Values

Caffeine does not support storing `null` values. If a method returns `null`:

- With `MemoizeAlways` (default): the `put` call to Caffeine will throw, which the aspect catches and logs as a warning. The method will re-execute on every call.
- With `MemoizeNonNulls`: null results are not stored, so no error occurs. The method re-executes only for null results.

If your method can return `null`, use `criteria = MemoizeNonNulls.class` to avoid repeated cache-put warnings.

## Classes

### CaffeineMemoizationProviderFactory

```java
package space.hypercode.providers.caffeine;

public class CaffeineMemoizationProviderFactory implements MemoizationProviderFactory {
    public MemoizationProvider create(String memoizationName, Duration ttl, long maxSize);
}
```

### CaffeineMemoizationProvider

```java
package space.hypercode.providers.caffeine;

public class CaffeineMemoizationProvider extends MemoizationProvider {
    public Optional<Object> getValueIfPresent(String key);
    public void put(String key, Object value);
    public void evictIfPresent(String key);
}
```

## Thread Safety

Caffeine caches are fully thread-safe. Multiple threads can read from and write to the same cache concurrently without external synchronization.
