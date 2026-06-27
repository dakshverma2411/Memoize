---
layout: default
title: Examples
nav_order: 7
---

# Examples

Practical code examples for common Memoize use cases. All examples assume you have completed the [Installation]({% link installation.md %}) steps and have AspectJ compile-time weaving configured.

## Basic Memoization with Memoizable Interface

The simplest approach: have your method argument implement `Memoizable` so key resolution is automatic.

```java
import space.hypercode.core.models.Memoizable;

public class ProductId implements Memoizable {
    private final long id;

    public ProductId(long id) {
        this.id = id;
    }

    @Override
    public String memoizationKey() {
        return String.valueOf(id);
    }

    public long getId() {
        return id;
    }
}
```

```java
import space.hypercode.core.annotations.MemoizeThis;

public class ProductService {

    @MemoizeThis(ttlInMs = 60000, size = 1000)
    public Product findProduct(ProductId id) {
        // Executes on cache miss; cached for 60 seconds
        return database.queryProduct(id.getId());
    }
}
```

No converter class needed -- Memoize detects that `ProductId` implements `Memoizable` and calls `memoizationKey()` automatically.

## Custom Key Converter

For arguments that you don't control (or don't want to make `Memoizable`), use a `SingleArgMemoizationKeyConverter`:

```java
import space.hypercode.core.converters.SingleArgMemoizationKeyConverter;

public class LongConverter implements SingleArgMemoizationKeyConverter<Long> {
    @Override
    public String toKey(Long input) {
        return input.toString();
    }
}
```

```java
@MemoizeThis(ttlInMs = 60000, size = 100, converter = LongConverter.class)
public long square(Long x) {
    return x * x;
}
```

## Multi-Argument Key Converter

For methods with multiple arguments, implement `MultiArgMemoizationKeyConverter`:

```java
import space.hypercode.core.converters.MultiArgMemoizationKeyConverter;

public class UserRoleConverter implements MultiArgMemoizationKeyConverter {
    @Override
    public String toKey(Object... args) {
        Long userId = (Long) args[0];
        String role = (String) args[1];
        return userId + ":" + role;
    }
}
```

```java
@MemoizeThis(ttlInMs = 30000, size = 500, converter = UserRoleConverter.class)
public List<Permission> getPermissions(Long userId, String role) {
    return database.queryPermissions(userId, role);
}
```

## TTL and Size Configuration

```java
// Short TTL for frequently changing data
@MemoizeThis(ttlInMs = 5000, size = 100)
public StockPrice getCurrentPrice(StockId id) { ... }

// Long TTL for rarely changing data
@MemoizeThis(ttlInMs = 3600000, size = 50)
public AppConfig loadConfig(ConfigKey key) { ... }

// No TTL (entries never expire), bounded by size
@MemoizeThis(size = 200)
public ComputeResult expensiveCalculation(InputId id) { ... }

// No TTL, no size limit (unbounded -- use with caution)
@MemoizeThis
public StaticData loadStaticData(DataId id) { ... }
```

## Eligibility Criteria

### MemoizeNonNulls -- Skip Null Results

Use `MemoizeNonNulls` to avoid caching `null` return values. This is recommended when using the Caffeine provider, which does not support storing null values.

```java
import space.hypercode.core.eligibility.MemoizeNonNulls;

@MemoizeThis(ttlInMs = 60000, size = 100, criteria = MemoizeNonNulls.class)
public User findUser(UserId id) {
    User user = database.queryUser(id);
    // If user is null (not found), the result is not cached.
    // If user is non-null, it is cached normally.
    return user;
}
```

### Custom Eligibility Criteria

Implement `EligibilityCriteria` for fine-grained control over what gets cached:

```java
import space.hypercode.core.eligibility.EligibilityCriteria;
import space.hypercode.core.models.MemoizeCallContext;

public class CacheOnlyNonEmpty implements EligibilityCriteria {
    @Override
    public boolean shouldMemoize(MemoizeCallContext context) {
        Object result = context.getReturnValue();
        if (result instanceof Collection<?> c) {
            return !c.isEmpty();
        }
        return result != null;
    }
}
```

```java
@MemoizeThis(ttlInMs = 60000, size = 100, criteria = CacheOnlyNonEmpty.class)
public List<Order> getRecentOrders(CustomerId id) {
    return database.queryRecentOrders(id);
    // Empty lists are not cached; non-empty lists are cached.
}
```

The `MemoizeCallContext` provides:
- `getReturnValue()` -- the value returned by the method
- `getArgs()` -- the original method arguments

## Config-Based Caching

Use `MemoizationConfig` to define caching settings programmatically at startup rather than in annotations. This is useful when you want to configure caching externally or share settings across methods.

### Define and register a config

```java
import space.hypercode.core.configs.MemoizationConfig;
import space.hypercode.core.configs.MemoizationConfigs;

MemoizationConfigs configs = new MemoizationConfigs();

configs.add("order-cache", MemoizationConfig.builder()
    .ttl(Duration.ofMinutes(5))
    .maxSize(2000)
    .converter(new OrderIdConverter())
    .eligibilityCriteria(MemoizeNonNulls.getInstance())
    .build());

Memoize.create()
    .scanIn("com.myapp")
    .configs(configs)
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .build()
    .start();
```

### Reference the config from the annotation

```java
@MemoizeThis(name = "order-cache", useConfig = true)
public Order findOrder(OrderId id) {
    return database.queryOrder(id);
}
```

The `name` must match the key used in `configs.add(...)`. If no config is found for the name, startup validation will fail.

## Full Application Example

A complete example showing initialization and usage:

```java
import space.hypercode.core.Memoize;
import space.hypercode.core.annotations.MemoizeThis;
import space.hypercode.core.models.Memoizable;
import space.hypercode.providers.caffeine.CaffeineMemoizationProviderFactory;
import com.codahale.metrics.MetricRegistry;
import space.hypercode.metrics.dw.DropwizardMemoizationMetrics;

// 1. Define a Memoizable argument type
public class MyLong implements Memoizable {
    private final Long value;

    public MyLong(Long value) {
        this.value = value;
    }

    @Override
    public String memoizationKey() {
        return value.toString();
    }

    public Long getValue() {
        return value;
    }
}

// 2. Create a service with memoized methods
public class MathService {

    @MemoizeThis(ttlInMs = 10000, size = 100)
    public MyLong square(MyLong x) {
        System.out.println("Computing square of " + x.getValue());
        return new MyLong(x.getValue() * x.getValue());
    }
}

// 3. Initialize and use
public class Application {
    public static void main(String[] args) {
        MetricRegistry metricRegistry = new MetricRegistry();

        Memoize.create()
            .scanIn("com.myapp")
            .providerFactory(new CaffeineMemoizationProviderFactory())
            .metrics(new DropwizardMemoizationMetrics(metricRegistry))
            .build()
            .start();

        MathService service = new MathService();

        // First call: prints "Computing square of 5", caches result
        MyLong result1 = service.square(new MyLong(5L));

        // Second call: returns cached result, no print
        MyLong result2 = service.square(new MyLong(5L));

        // Different argument: prints "Computing square of 10", caches result
        MyLong result3 = service.square(new MyLong(10L));
    }
}
```

## Named Caches

Use `name` to give a cache a specific name, which is useful for metrics and debugging:

```java
@MemoizeThis(name = "user-profile-cache", ttlInMs = 300000, size = 1000)
public UserProfile getProfile(UserId id) { ... }

@MemoizeThis(name = "user-settings-cache", ttlInMs = 600000, size = 500)
public UserSettings getSettings(UserId id) { ... }
```

Each named cache is a separate cache instance with its own TTL, size, and metrics.
