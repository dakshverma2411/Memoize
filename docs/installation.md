---
layout: default
title: Installation
nav_order: 3
---

# Installation

## Requirements

- **Java 17** or later
- **Maven 3.9+** or Gradle 8+
- **AspectJ compile-time weaving** -- required for `@MemoizeThis` to work

## Maven

### Using the BOM (recommended)

The BOM manages versions for all Memoize modules:

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
```

Then add the modules you need (versions are managed by the BOM):

```xml
<dependencies>
    <!-- Core library (required) -->
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-core</artifactId>
    </dependency>

    <!-- Caffeine cache backend (required unless you provide a custom backend) -->
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-caffeine</artifactId>
    </dependency>

    <!-- Dropwizard Metrics integration (optional) -->
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-dw-metrics</artifactId>
    </dependency>
</dependencies>
```

### Without the BOM

Specify versions explicitly on each dependency:

```xml
<dependencies>
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-core</artifactId>
        <version>0.1.0-alpha.1</version>
    </dependency>
    <dependency>
        <groupId>space.hypercode</groupId>
        <artifactId>memoize-caffeine</artifactId>
        <version>0.1.0-alpha.1</version>
    </dependency>
</dependencies>
```

## Gradle

```groovy
dependencies {
    implementation platform('space.hypercode:memoize-bom:0.1.0-alpha.1')

    implementation 'space.hypercode:memoize-core'
    implementation 'space.hypercode:memoize-caffeine'

    // Optional
    implementation 'space.hypercode:memoize-dw-metrics'
}
```

Kotlin DSL:

```kotlin
dependencies {
    implementation(platform("space.hypercode:memoize-bom:0.1.0-alpha.1"))

    implementation("space.hypercode:memoize-core")
    implementation("space.hypercode:memoize-caffeine")

    // Optional
    implementation("space.hypercode:memoize-dw-metrics")
}
```

## AspectJ Weaving (Required)

Memoize uses compile-time AspectJ weaving. Without it, `@MemoizeThis` annotations have no effect.

### Maven -- aspectj-maven-plugin

Add the AspectJ Maven plugin to your project's `pom.xml`. This performs post-compile weaving: `javac` compiles first (preserving Lombok and annotation processor output), then `ajc` weaves the `MemoizeAspect` into the compiled bytecode.

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

You also need the AspectJ runtime on your classpath:

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.22.1</version>
</dependency>
```

### Gradle -- io.freefair.aspectj

For Gradle projects, use the FreeFair AspectJ plugin:

```groovy
plugins {
    id 'io.freefair.aspectj.post-compile-weaving' version '8.6'
}

dependencies {
    aspect 'space.hypercode:memoize-core'
}
```

### Weaving for Tests

To also weave test classes (so `@MemoizeThis` works in test code), add a test-compile execution in Maven:

```xml
<execution>
    <id>post-compile-weave-tests</id>
    <phase>process-test-classes</phase>
    <goals>
        <goal>test-compile</goal>
    </goals>
</execution>
```

### Verifying Weaving

When `showWeaveInfo` is enabled, you should see output like this during the build:

```
[INFO] Join point 'method-execution(...)' in Type '...' (MyService.java:12)
  advised by around advice from 'space.hypercode.core.aspect.MemoizeAspect' (MemoizeAspect.java:44)
```

If you do not see any weaving output, check that:
1. The `aspectj-maven-plugin` is configured correctly
2. `memoize-core` is listed as an aspect library
3. Your annotated classes are in the weave directory

## Module Summary

| Module | GroupId | ArtifactId | Required? |
|---|---|---|---|
| Core | `space.hypercode` | `memoize-core` | Yes |
| Caffeine | `space.hypercode` | `memoize-caffeine` | Yes (unless custom provider) |
| Dropwizard Metrics | `space.hypercode` | `memoize-dw-metrics` | No |
| BOM | `space.hypercode` | `memoize-bom` | No (recommended) |
