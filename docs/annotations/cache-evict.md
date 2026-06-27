---
layout: default
title: "@CacheEvict"
parent: Annotations
nav_order: 2
---

# @CacheEvict

{: .label .label-yellow }
Planned

{: .warning }
> This annotation is not yet implemented. It is on the roadmap for a future release.

## Overview

`@CacheEvict` will allow you to evict specific cache entries programmatically by annotating a method. When the annotated method is called, the corresponding cache entry (identified by the method arguments) will be removed from the cache.

## Planned Usage

```java
@CacheEvict(name = "user-cache")
public void deleteUser(UserId id) {
    database.deleteUser(id);
    // The cache entry for this UserId will be evicted after the method executes
}
```

## Planned Behavior

- Evict a single cache entry by key (derived from method arguments)
- Option to evict all entries in a named cache
- Eviction happens after the method executes successfully
- If the method throws an exception, the cache entry is not evicted

## Tracking

Follow the [GitHub Issues](https://github.com/dakshverma2411/memoize/issues) for updates on this feature.
