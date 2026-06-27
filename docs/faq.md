---
layout: default
title: FAQ
nav_order: 9
---

# FAQ

## Why compile-time weaving instead of runtime proxies?

Compile-time AspectJ weaving modifies the bytecode at build time, so there is no runtime proxy creation, no reflection, and no dynamic dispatch overhead on every call. This results in:

- Lower per-call overhead compared to runtime AOP (e.g., Spring AOP's JDK dynamic proxies or CGLIB proxies)
- No surprises with self-invocation -- since the weaving is in the bytecode, calling a memoized method from within the same class works correctly (unlike proxy-based AOP where `this.method()` bypasses the proxy)
- No requirement for interfaces or non-final classes

The tradeoff is that you must configure the AspectJ compiler plugin in your build.

## Does it work with Spring?

Yes, Memoize works in Spring applications as long as AspectJ compile-time (or post-compile) weaving is configured in the build. Since the weaving happens at build time, it does not conflict with Spring's own AOP infrastructure.

Note that Memoize is **not** Spring Cache (`@Cacheable`). It is a standalone library. You can use both in the same project if needed, though that would be unusual.

## What happens if Memoize is not started?

If `Memoize.start()` has not been called, all `@MemoizeThis` methods execute normally without caching. The aspect checks for the `Memoize` singleton instance on every call; if it is `null`, the method proceeds directly. A warning is logged once.

This means it is safe to use `@MemoizeThis` in library code that may or may not have Memoize initialized -- there is no risk of exceptions or failures.

## Can I cache null values?

It depends on the cache provider:

- **Caffeine** does not support `null` values. If you attempt to cache a `null`, Caffeine throws a `NullPointerException`, which the aspect catches and logs as a warning. The method will re-execute on every call for null results.
- To handle this correctly, use `criteria = MemoizeNonNulls.class` to skip caching null return values:

```java
@MemoizeThis(ttlInMs = 60000, size = 100, criteria = MemoizeNonNulls.class)
public User findUser(UserId id) {
    return database.queryUser(id);  // may return null
}
```

A custom provider could support null values if the underlying cache backend supports them.

## How does key generation work?

Cache keys are generated from the method arguments, resolved in this order:

1. **`useConfig=true`** -- the converter is taken from the registered `MemoizationConfig` for the cache name
2. **Explicit `converter` in annotation** -- the specified `MemoizationKeyConverter` class is instantiated (cached after first use) and used to convert arguments to a key string
3. **`Memoizable` interface** -- if the method has a single argument that implements `Memoizable`, its `memoizationKey()` method is called to produce the key
4. **No converter found** -- caching is skipped entirely for that method; it executes normally every time

There is no `toString()` fallback. If none of the above conditions are met, the method runs without caching and a warning is logged at startup.

## Is it thread-safe?

Yes. The caching layer is thread-safe:

- The `MemoizeAspect` uses `ConcurrentHashMap` for provider instance caching
- Caffeine caches are fully concurrent (lock-free reads, concurrent writes)
- `ConverterResolver` and `EligibilityCriteriaResolver` cache resolved instances in `ConcurrentHashMap`
- The `MemoizationConfigs` registry uses `ConcurrentHashMap`

Multiple threads can call the same `@MemoizeThis` method concurrently without external synchronization.

Note: There is no "cache stampede" protection in the current version. If multiple threads call the same method with the same arguments simultaneously and all miss the cache, all threads will execute the method. Only one result will end up in the cache (last write wins), but all threads will return their own computed result.

## Can I use it with methods that throw exceptions?

Yes. If a `@MemoizeThis` method throws an exception:

- The exception propagates to the caller normally
- Nothing is stored in the cache
- The cache state is not corrupted

```java
@MemoizeThis(ttlInMs = 60000, size = 100)
public Result compute(InputId id) {
    if (id.getValue() < 0) {
        throw new IllegalArgumentException("Negative ID");
    }
    return doComputation(id);
}
```

## Can I have multiple Memoize instances?

No. `Memoize` is a singleton per JVM. Calling `Memoize.create().build().start()` a second time will throw an `IllegalStateException`. This is by design -- the AspectJ aspect accesses the singleton via `Memoize.getInstance()`.

## What if my converter fails?

If key generation throws an exception, the aspect catches it, logs a warning, and proceeds without caching (the method executes normally). This applies to both `SingleArgMemoizationKeyConverter.toKey()` and `MultiArgMemoizationKeyConverter.toKey()`.

## Does it work with Lombok?

Yes. The recommended AspectJ configuration uses **post-compile weaving** (the `process-classes` phase), which runs after `javac` and Lombok annotation processing. This means Lombok-generated code (getters, builders, `@Value`, etc.) is fully compatible.

## What is the `scanIn` package for?

The `scanIn` package tells `Memoize.start()` where to scan for `@MemoizeThis` annotations during startup validation. It uses ClassGraph to find annotated methods and validate their configuration (e.g., checking that converters have no-arg constructors, `useConfig=true` methods have registered configs).

At runtime, the AspectJ aspect intercepts all `@MemoizeThis` methods regardless of package -- the `scanIn` package only affects startup validation.
