---
layout: default
title: Redis
parent: Providers
nav_order: 2
---

# Redis Provider

{: .label .label-yellow }
Planned

{: .warning }
> The Redis provider is not yet implemented. It is on the roadmap for a future release.

## Overview

A Redis-backed provider would enable distributed caching for Memoize, allowing cached values to be shared across multiple JVM instances. This is useful for:

- **Microservices** -- Share cached results across service instances
- **Horizontal scaling** -- Cache survives individual instance restarts
- **Large datasets** -- Offload memory pressure from the JVM heap

## Planned Design

The Redis provider would implement the same `MemoizationProviderFactory` and `MemoizationProvider` interfaces:

```java
public class RedisMemoizationProviderFactory implements MemoizationProviderFactory {
    public RedisMemoizationProviderFactory(RedisClient client) { ... }

    @Override
    public MemoizationProvider create(String memoizationName, Duration ttl, long maxSize) {
        return new RedisMemoizationProvider(client, memoizationName, ttl, maxSize);
    }
}
```

### Considerations

- **Serialization**: Values would need to be serialized/deserialized for Redis storage. A pluggable serializer interface is likely.
- **TTL**: Redis natively supports key-level TTL via `EXPIRE` / `PEXPIRE`.
- **Max size**: Redis does not natively enforce a max cache size per key prefix. This may be handled at the application level or via Redis memory policies.
- **Null values**: Would need a sentinel representation since Redis keys cannot hold null.

## Tracking

Follow the [GitHub Issues](https://github.com/dakshverma2411/memoize/issues) for updates on this feature.

## Alternative: Custom Provider

In the meantime, you can implement your own Redis-backed provider. See the [Custom Provider]({% link providers/custom.md %}) guide for instructions.
