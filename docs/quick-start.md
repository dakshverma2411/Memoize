---
layout: default
title: Quick Start
nav_order: 2
---

# Quick Start

Get Memoize running in your project in three steps.

## 1. Add Dependencies

Add the BOM and required modules to your `pom.xml`:

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

## 2. Configure AspectJ Compile-Time Weaving

Memoize requires compile-time (or post-compile) AspectJ weaving. Add the plugin to your build:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.aspectj</groupId>
            <artifactId>aspectj-maven-plugin</artifactId>
            <version>1.14.1</version>
            <configuration>
                <complianceLevel>17</complianceLevel>
                <source>17</source>
                <target>17</target>
                <showWeaveInfo>true</showWeaveInfo>
                <forceAjcCompile>true</forceAjcCompile>
                <sources/>
                <weaveDirectories>
                    <weaveDirectory>${project.build.outputDirectory}</weaveDirectory>
                </weaveDirectories>
                <aspectLibraries>
                    <aspectLibrary>
                        <groupId>space.hypercode</groupId>
                        <artifactId>memoize-core</artifactId>
                    </aspectLibrary>
                </aspectLibraries>
            </configuration>
            <executions>
                <execution>
                    <id>post-compile-weave</id>
                    <phase>process-classes</phase>
                    <goals>
                        <goal>compile</goal>
                    </goals>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>org.aspectj</groupId>
                    <artifactId>aspectjtools</artifactId>
                    <version>1.9.22.1</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

## 3. Annotate, Initialize, and Use

### Annotate a method

```java
import space.hypercode.core.annotations.MemoizeThis;
import space.hypercode.core.models.Memoizable;

// Option A: argument implements Memoizable (auto key resolution)
public class UserId implements Memoizable {
    private final long id;

    public UserId(long id) {
        this.id = id;
    }

    @Override
    public String memoizationKey() {
        return String.valueOf(id);
    }
}

public class UserService {

    @MemoizeThis(ttlInMs = 60000, size = 500)
    public User findUser(UserId userId) {
        // This result will be cached for 60 seconds
        return database.queryUser(userId);
    }
}
```

### Initialize Memoize at startup

```java
import space.hypercode.core.Memoize;
import space.hypercode.providers.caffeine.CaffeineMemoizationProviderFactory;

public class Application {
    public static void main(String[] args) {
        Memoize memoize = Memoize.create()
            .scanIn("com.myapp")                                   // package to scan for @MemoizeThis
            .providerFactory(new CaffeineMemoizationProviderFactory()) // cache backend
            .build();

        memoize.start();

        // Now all @MemoizeThis methods in com.myapp.** are cached
        UserService service = new UserService();
        service.findUser(new UserId(42));  // executes method, caches result
        service.findUser(new UserId(42));  // returns cached result
    }
}
```

The `scanIn` package tells Memoize where to look for `@MemoizeThis` annotations during startup validation. At runtime, the AspectJ aspect intercepts annotated methods regardless of package.

## What Happens at Startup

When you call `memoize.start()`, the library:

1. Scans the specified package for `@MemoizeThis`-annotated methods
2. Validates that each method has a resolvable key converter
3. Checks that `useConfig=true` methods have registered configs
4. Logs warnings for methods that will be skipped (e.g., no converter, void return type)
5. Fails fast with clear error messages if configuration is invalid

## Next Steps

- [Installation]({% link installation.md %}) -- Full dependency details and Gradle setup
- [Annotations]({% link annotations/index.md %}) -- `@MemoizeThis` attribute reference
- [Examples]({% link examples.md %}) -- More usage patterns
