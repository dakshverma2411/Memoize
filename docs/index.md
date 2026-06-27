---
layout: default
title: Home
nav_order: 1
---

# Memoize

**Annotation-driven memoization for Java 17+ with compile-time AspectJ weaving.**

Memoize lets you cache method return values by adding a single annotation. It uses compile-time AspectJ weaving for zero runtime proxy overhead, pluggable cache backends, and built-in metrics support.

```java
@MemoizeThis(ttlInMs = 60000, size = 1000, converter = UserIdConverter.class)
public User findUser(Long userId) {
    return database.queryUser(userId);
}
```

{: .warning }
> **Alpha Release** -- The API is subject to change. Not yet recommended for production use.

## Key Features

- **`@MemoizeThis` annotation** -- Mark any method for transparent result caching
- **Compile-time weaving** -- AspectJ weaves caching logic at build time, no runtime proxies
- **Pluggable cache backends** -- Ships with Caffeine; implement your own with two interfaces
- **Pluggable metrics** -- Dropwizard Metrics integration included, or bring your own
- **Fail-safe design** -- Cache failures never crash your application; methods fall through normally
- **Startup validation** -- Classpath scanning at startup detects misconfiguration early
- **Flexible key resolution** -- Auto-resolves keys from the `Memoizable` interface or custom converters

## Modules

| Module | Description |
|---|---|
| `memoize-core` | Core library: annotation, aspect, provider interfaces, key converters |
| `memoize-caffeine` | Cache backend using [Caffeine](https://github.com/ben-manes/caffeine) |
| `memoize-dw-metrics` | Metrics via [Dropwizard Metrics](https://metrics.dropwizard.io/) |
| `memoize-bom` | Bill of Materials for dependency management |
| `memoize-parent` | Parent POM with shared build configuration |
| `memoize-examples` | Usage examples and integration tests |

## How It Works

```
@MemoizeThis annotation
        |
        v
MemoizeAspect (AspectJ @Around)
        |
        +---> ConverterResolver --> cache key string
        |
        +---> MemoizationProvider (e.g. Caffeine)
        |         |
        |         +---> hit:  return cached value
        |         +---> miss: execute method, cache result
        |
        +---> MemoizationMetrics (record hit/miss/put/eviction)
```

## Next Steps

- [Quick Start]({% link quick-start.md %}) -- Get up and running in minutes
- [Installation]({% link installation.md %}) -- Detailed dependency and build setup
- [Examples]({% link examples.md %}) -- Code examples for common use cases

## License

Memoize is licensed under the [Apache License 2.0](https://github.com/dakshverma2411/memoize/blob/main/LICENSE).
