---
layout: default
title: "@CachePut"
parent: Annotations
nav_order: 3
---

# @CachePut

{: .label .label-yellow }
Planned

{: .warning }
> This annotation is not yet implemented. It is on the roadmap for a future release.

## Overview

`@CachePut` will allow you to always execute the method and update the cache with the result, regardless of whether a cached value already exists. Unlike `@MemoizeThis`, which skips method execution on a cache hit, `@CachePut` always runs the method body and stores the return value.

## Planned Usage

```java
@CachePut(name = "user-cache")
public User updateUser(UserId id, UserData data) {
    User updated = database.updateUser(id, data);
    // The cache entry for this UserId will be updated with the new value
    return updated;
}
```

## Planned Behavior

- The method always executes, regardless of cache state
- The return value is stored in the cache, replacing any existing entry
- Useful for write-through caching patterns where you want to keep the cache in sync with mutations
- Can be combined with `@MemoizeThis` on the read path for a full read/write caching strategy

## Tracking

Follow the [GitHub Issues](https://github.com/dakshverma2411/memoize/issues) for updates on this feature.
