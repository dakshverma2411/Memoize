---
layout: default
title: Benchmarks
nav_order: 8
---

# Benchmarks

{: .label .label-yellow }
Planned

{: .warning }
> Benchmarks are not yet available. This page will be updated with results once they are complete.

## Planned Approach

Benchmarks will use [JMH](https://github.com/openjdk/jmh) (Java Microbenchmark Harness) to measure the overhead introduced by Memoize's caching layer.

### What Will Be Measured

- **Cache hit latency** -- Time to retrieve a value from the cache (aspect overhead + Caffeine lookup)
- **Cache miss latency** -- Time for a cache miss, method execution, and cache store
- **Overhead vs. direct call** -- Comparison of calling a memoized method vs. calling it directly without the aspect
- **Throughput** -- Operations per second under concurrent access

### Planned Comparisons

| Scenario | Description |
|---|---|
| No memoization | Direct method call, no aspect |
| Memoize (cache hit) | Method call with warmed cache |
| Memoize (cache miss) | Method call with cold cache |
| Memoize (mixed) | Realistic workload with a mix of hits and misses |

### Expected Characteristics

- **Cache hit path**: Expected to add sub-microsecond overhead (aspect dispatch + `ConcurrentHashMap` lookup + Caffeine lookup)
- **Cache miss path**: Overhead is the aspect dispatch + key generation + cache put, on top of the original method execution time
- **Compile-time weaving**: No runtime proxy creation or reflection overhead, unlike annotation-based AOP frameworks that use dynamic proxies

## Tracking

Follow the [GitHub Issues](https://github.com/dakshverma2411/memoize/issues) for updates on benchmarks.
