---
layout: default
title: "@MemoizeThis"
parent: Annotations
nav_order: 1
---

# @MemoizeThis

Marks a method for memoization (caching of return values). When the method is called, the aspect checks the cache first and returns the cached value on a hit, or executes the method and caches the result on a miss.

```java
import space.hypercode.core.annotations.MemoizeThis;

@MemoizeThis(ttlInMs = 60000, size = 1000)
public User findUser(UserId id) {
    return database.queryUser(id);
}
```

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `name` | `String` | `""` (auto-generated) | Cache name. If empty, derived from the fully qualified method signature. |
| `ttlInMs` | `long` | `0` | Time-to-live in milliseconds. `0` means no expiration. |
| `size` | `long` | `0` | Maximum number of cache entries. `0` means unbounded. |
| `converter` | `Class<? extends MemoizationKeyConverter>` | `MemoizationKeyConverter.class` | Key converter class for generating cache keys from arguments. |
| `criteria` | `Class<? extends EligibilityCriteria>` | `MemoizeAlways.class` | Eligibility criteria that determines whether a result should be cached. |
| `useConfig` | `boolean` | `false` | If `true`, the converter, TTL, size, and criteria are read from a registered `MemoizationConfig` instead of from the annotation. |

## Attribute Details

### `name`

A unique name for the cache. If not specified, it is auto-generated from the method signature in the format `com.example.MyClass.myMethod(java.lang.Long)`.

```java
// Explicit name
@MemoizeThis(name = "user-cache", ttlInMs = 60000, size = 1000)
public User findUser(UserId id) { ... }

// Auto-generated name (derived from method signature)
@MemoizeThis(ttlInMs = 60000, size = 1000)
public User findUser(UserId id) { ... }
```

### `ttlInMs`

Time-to-live for cache entries in milliseconds. After this duration, entries expire and the method will be re-executed on the next call. A value of `0` means entries never expire (subject to the cache provider's behavior).

```java
@MemoizeThis(ttlInMs = 30000)  // 30 second TTL
public Price getCurrentPrice(ProductId id) { ... }
```

### `size`

Maximum number of entries in the cache. When the limit is reached, the cache provider evicts entries according to its eviction policy (Caffeine uses a window TinyLFU policy). A value of `0` means unbounded.

```java
@MemoizeThis(ttlInMs = 60000, size = 500)  // max 500 entries
public User findUser(UserId id) { ... }
```

### `converter`

Specifies the class that converts method arguments into a cache key string. The converter must implement `SingleArgMemoizationKeyConverter<T>` (for single-argument methods) or `MultiArgMemoizationKeyConverter` (for multi-argument methods) and have a public no-arg constructor.

```java
public class LongConverter implements SingleArgMemoizationKeyConverter<Long> {
    @Override
    public String toKey(Long input) {
        return input.toString();
    }
}

@MemoizeThis(ttlInMs = 60000, size = 100, converter = LongConverter.class)
public long square(Long x) {
    return x * x;
}
```

If no converter is specified and the argument does not implement `Memoizable`, caching is skipped.

### `criteria`

Determines whether a method result is eligible for caching. The default `MemoizeAlways` caches every result. Use `MemoizeNonNulls` to skip caching `null` return values.

```java
import space.hypercode.core.eligibility.MemoizeNonNulls;

@MemoizeThis(ttlInMs = 60000, size = 100, criteria = MemoizeNonNulls.class)
public User findUser(UserId id) {
    // null results will NOT be cached; non-null results will be cached
    return database.queryUser(id);
}
```

You can implement `EligibilityCriteria` for custom logic:

```java
import space.hypercode.core.eligibility.EligibilityCriteria;
import space.hypercode.core.models.MemoizeCallContext;

public class CacheOnlySuccessful implements EligibilityCriteria {
    @Override
    public boolean shouldMemoize(MemoizeCallContext context) {
        Object result = context.getReturnValue();
        return result instanceof Result r && r.isSuccess();
    }
}

@MemoizeThis(ttlInMs = 60000, criteria = CacheOnlySuccessful.class)
public Result processOrder(OrderId id) { ... }
```

### `useConfig`

When `true`, the method reads its converter, TTL, size, and eligibility criteria from a `MemoizationConfig` registered under the cache `name`, instead of from the annotation attributes.

```java
// Register config at startup
MemoizationConfigs configs = new MemoizationConfigs();
configs.add("order-cache", MemoizationConfig.builder()
    .ttl(Duration.ofMinutes(5))
    .maxSize(1000)
    .converter(new OrderIdConverter())
    .eligibilityCriteria(MemoizeNonNulls.getInstance())
    .build());

Memoize.create()
    .scanIn("com.myapp")
    .configs(configs)
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .build()
    .start();

// In your service class
@MemoizeThis(name = "order-cache", useConfig = true)
public Order findOrder(OrderId id) { ... }
```

If `useConfig=true` but no config is registered for the name, startup validation will fail with an error.

## Key Resolution Order

When a `@MemoizeThis` method is called, the cache key is resolved in this order:

1. **`useConfig=true`** -- converter from the registered `MemoizationConfig`
2. **Explicit `converter`** -- the class specified in the annotation, instantiated via reflection (cached after first use)
3. **`Memoizable` interface** -- if the method has a single argument that implements `Memoizable`, its `memoizationKey()` method is called
4. **No converter found** -- caching is skipped entirely; the method executes normally

```java
// Resolution path 1: useConfig
@MemoizeThis(name = "my-cache", useConfig = true)
public Result compute(Input in) { ... }

// Resolution path 2: explicit converter
@MemoizeThis(converter = LongConverter.class)
public long square(Long x) { ... }

// Resolution path 3: Memoizable interface
@MemoizeThis(ttlInMs = 60000, size = 100)
public MyLong square(MyLong x) { ... }  // MyLong implements Memoizable

// Resolution path 4: no converter -- caching skipped
@MemoizeThis(ttlInMs = 60000, size = 100)
public String upper(String x) { ... }  // String doesn't implement Memoizable
```

## Cache Name Resolution

If `name` is empty (the default), the cache name is auto-generated from the method signature:

```
<fully.qualified.ClassName>.<methodName>(<param1Type>,<param2Type>)
```

For example, `com.myapp.UserService.findUser(com.myapp.UserId)`.

## Behavior Notes

- If `Memoize.start()` has not been called, all `@MemoizeThis` methods execute normally without caching.
- Exceptions thrown by the original method propagate to the caller and are never cached.
- Cache lookup or storage failures are logged as warnings and result in a passthrough to the original method.
- The aspect never throws exceptions that affect application behavior.
