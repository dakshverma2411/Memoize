# Memoize

An annotation-driven memoization library for Java 17+. Cache method results transparently using compile-time AspectJ weaving with pluggable cache backends and metrics.

## Features

- **Annotation-driven** - Add `@MemoizeThis` to any method to enable caching
- **Compile-time weaving** - Zero runtime proxy overhead via AspectJ
- **Pluggable backends** - Ship with Caffeine support, easily extensible
- **Pluggable metrics** - Dropwizard Metrics integration included
- **Fail-safe** - Cache failures never crash your application; methods execute normally
- **Startup validation** - Detects misconfiguration early via classpath scanning
- **Flexible key resolution** - Auto-resolves keys from `Memoizable` interface or custom converters

## Modules

| Module | Description |
|--------|-------------|
| `memoize-core` | Core library: annotation, aspect, provider interfaces, key converters |
| `memoize-caffeine` | Cache backend using [Caffeine](https://github.com/ben-manes/caffeine) |
| `memoize-dw-metrics` | Metrics via [Dropwizard Metrics](https://metrics.dropwizard.io/) |
| `memoize-bom` | Bill of Materials for dependency management |
| `memoize-parent` | Parent POM with shared build configuration |
| `memoize-examples` | Usage examples |

## Quick Start

### Installation

Add the BOM and core dependency to your `pom.xml`:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>space.hypercode</groupId>
            <artifactId>memoize-bom</artifactId>
            <version>0.1.0-alpha.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-core</artifactId>
    </dependency>
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-caffeine</artifactId>
    </dependency>
</dependencies>
```

### Usage

#### 1. Annotate methods

```java
@MemoizeThis(name = "user-cache", ttlInMs = 60000, size = 1000)
public User findUser(Long userId) {
    return database.queryUser(userId);
}
```

#### 2. Implement `Memoizable` for automatic key resolution

```java
public class UserId implements Memoizable {
    private final long id;

    @Override
    public String memoizationKey() {
        return String.valueOf(id);
    }
}
```

#### 3. Or provide a custom key converter

```java
public class LongConverter implements SingleArgMemoizationKeyConverter<Long> {
    @Override
    public String convert(Long value) {
        return String.valueOf(value);
    }
}

@MemoizeThis(name = "user-cache", converter = LongConverter.class)
public User findUser(Long userId) { ... }
```

#### 4. Initialize at application startup

```java
Memoize memoize = Memoize.create()
    .scanIn("com.myapp")
    .providerFactory(new CaffeineMemoizationProviderFactory())
    .metrics(new DropwizardMemoizationMetrics(metricRegistry))
    .build();

memoize.start();
```

### AspectJ Compile-Time Weaving

Add the AspectJ Maven plugin to your build:

```xml
<plugin>
    <groupId>dev.aspectj</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.1</version>
    <configuration>
        <complianceLevel>17</complianceLevel>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>space.hypercode</groupId>
                <artifactId>memoize-core</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Building

```bash
mvn clean verify
```

Requires Java 17+.

## Architecture

```
@MemoizeThis annotation
        |
        v
MemoizeAspect (AspectJ @Around)
        |
        +---> ConverterResolver --> key string
        |
        +---> MemoizationProvider (e.g. Caffeine)
        |         |
        |         +---> hit: return cached value
        |         +---> miss: proceed, cache result
        |
        +---> MemoizationMetrics (record hit/miss/put/eviction)
```

## Extending

### Custom Cache Backend

Implement `MemoizationProvider` and `MemoizationProviderFactory`:

```java
public class RedisMemoizationProviderFactory implements MemoizationProviderFactory {
    @Override
    public MemoizationProvider create(String name, long ttlMs, int maxSize) {
        return new RedisMemoizationProvider(name, ttlMs);
    }
}
```

### Custom Metrics

Implement the `MemoizationMetrics` interface to integrate with your monitoring system.

## License

TBD

## Status

**Alpha** - API is subject to change. Not yet recommended for production use.
