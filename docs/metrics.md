---
layout: default
title: Metrics
nav_order: 6
---

# Metrics

Memoize provides a pluggable metrics interface for monitoring cache performance. Metrics are scoped per cache name, so you can track hit rates, miss rates, and latency for each `@MemoizeThis` method independently.

## MemoizationMetrics Interface

```java
public interface MemoizationMetrics {
    void recordHit(String memoizationName);
    void recordMiss(String memoizationName);
    void recordPut(String memoizationName);
    void recordEviction(String memoizationName);
    void recordGetDuration(String memoizationName, long durationNanos);
    void recordPutDuration(String memoizationName, long durationNanos);
}
```

### What Is Tracked

| Metric | Type | Description |
|---|---|---|
| `recordHit` | Counter | A cached value was found and returned |
| `recordMiss` | Counter | No cached value was found; the method was executed |
| `recordPut` | Counter | A value was stored in the cache |
| `recordEviction` | Counter | A cache entry was evicted (for providers that support eviction callbacks) |
| `recordGetDuration` | Duration | Time taken for the cache lookup operation (nanoseconds) |
| `recordPutDuration` | Duration | Time taken for the cache store operation (nanoseconds) |

## Built-in Implementations

### NoOpMetrics (default)

If no metrics implementation is registered, `NoOpMetrics` is used. All methods are empty -- no metrics are collected or stored.

```java
// No metrics (default behavior)
Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .build()
    .start();
```

### DropwizardMemoizationMetrics

Integrates with [Dropwizard Metrics](https://metrics.dropwizard.io/) using a `MetricRegistry`.

#### Dependency

```xml
<dependency>
    <groupId>space.hypercode</groupId>
    <artifactId>memoize-dw-metrics</artifactId>
</dependency>
```

#### Usage

```java
import com.codahale.metrics.MetricRegistry;
import space.hypercode.metrics.dw.DropwizardMemoizationMetrics;

MetricRegistry metricRegistry = new MetricRegistry();

Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .metrics(new DropwizardMemoizationMetrics(metricRegistry))
    .build()
    .start();
```

#### Registered Metrics

For each cache name, the following metrics are registered in the `MetricRegistry`:

| Metric Name | Type | Description |
|---|---|---|
| `memoize.<cacheName>.hits` | Counter | Cache hit count |
| `memoize.<cacheName>.misses` | Counter | Cache miss count |
| `memoize.<cacheName>.puts` | Counter | Cache put count |
| `memoize.<cacheName>.evictions` | Counter | Cache eviction count |
| `memoize.<cacheName>.get` | Timer | Cache lookup duration |
| `memoize.<cacheName>.put` | Timer | Cache store duration |

For example, a method with `@MemoizeThis(name = "user-cache")` would produce metrics like `memoize.user-cache.hits`, `memoize.user-cache.misses`, etc.

## Custom Metrics Implementation

Implement the `MemoizationMetrics` interface to integrate with your own monitoring system:

```java
import space.hypercode.core.metrics.MemoizationMetrics;

public class PrometheusMetrics implements MemoizationMetrics {

    @Override
    public void recordHit(String memoizationName) {
        // Increment Prometheus counter
    }

    @Override
    public void recordMiss(String memoizationName) {
        // Increment Prometheus counter
    }

    @Override
    public void recordPut(String memoizationName) {
        // Increment Prometheus counter
    }

    @Override
    public void recordEviction(String memoizationName) {
        // Increment Prometheus counter
    }

    @Override
    public void recordGetDuration(String memoizationName, long durationNanos) {
        // Record duration in Prometheus histogram
    }

    @Override
    public void recordPutDuration(String memoizationName, long durationNanos) {
        // Record duration in Prometheus histogram
    }
}
```

Register it at startup:

```java
Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .metrics(new PrometheusMetrics())
    .build()
    .start();
```

## Fail-Safe Behavior

Metrics recording is wrapped in try-catch blocks within the `MemoizeAspect`. If your metrics implementation throws an exception, it is logged as a warning and does not affect caching or application behavior.
