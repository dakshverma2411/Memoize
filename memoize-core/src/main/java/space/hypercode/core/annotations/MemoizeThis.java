package space.hypercode.core.annotations;

import space.hypercode.core.converters.MemoizationKeyConverter;
import space.hypercode.core.eligibility.EligibilityCriteria;
import space.hypercode.core.eligibility.MemoizeAlways;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for transparent memoization (caching).
 *
 * <p>When a method annotated with {@code @MemoizeThis} is invoked, the framework
 * intercepts the call, generates a cache key from the method arguments, and returns
 * the cached result if available. Otherwise, the original method executes and its
 * result is stored in the cache.
 *
 * <p>Cache behavior can be configured either inline via annotation elements or
 * by referencing a named {@link space.hypercode.core.configs.MemoizationConfig}
 * when {@link #useConfig()} is set to {@code true}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemoizeThis {
    /**
     * Whether to use a named {@link space.hypercode.core.configs.MemoizationConfig}
     * instead of inline annotation values. When {@code true}, the configuration is
     * looked up by {@link #name()}.
     *
     * @return {@code true} to resolve settings from a registered config; defaults to {@code false}
     */
    boolean useConfig() default false;

    /**
     * The logical name of this cache. Used as the lookup key when {@link #useConfig()} is
     * {@code true}, and as the cache identifier in metrics. If empty, a name is derived
     * from the method's fully-qualified signature.
     *
     * @return the cache name; defaults to {@code ""}
     */
    String name() default "";

    /**
     * Time-to-live for cached entries, in milliseconds.
     * A value of {@code 0} means no TTL (entries do not expire).
     * Ignored when {@link #useConfig()} is {@code true}.
     *
     * @return the TTL in milliseconds; defaults to {@code 0}
     */
    long ttlInMs() default 0L;

    /**
     * Maximum number of entries in the cache.
     * A value of {@code 0} means no size limit.
     * Ignored when {@link #useConfig()} is {@code true}.
     *
     * @return the maximum cache size; defaults to {@code 0}
     */
    long size() default 0L;

    /**
     * The {@link MemoizationKeyConverter} class to use for generating cache keys from
     * method arguments. The class must have a public no-arg constructor.
     * If not specified, the framework attempts auto-resolution (e.g. via
     * {@link space.hypercode.core.models.Memoizable}).
     *
     * @return the converter class; defaults to {@link MemoizationKeyConverter} (sentinel for auto-resolution)
     */
    Class<? extends MemoizationKeyConverter> converter() default MemoizationKeyConverter.class;

    /**
     * The {@link EligibilityCriteria} class that determines whether a method's return value
     * should be cached. The class must have either a public static {@code getInstance()} method
     * or a public no-arg constructor.
     *
     * @return the eligibility criteria class; defaults to {@link MemoizeAlways}
     */
    Class<? extends EligibilityCriteria> criteria() default MemoizeAlways.class;
}
