package space.hypercode.core.annotations;

import space.hypercode.core.converters.MemoizationKeyConverter;
import space.hypercode.core.eligibility.EligibilityCriteria;
import space.hypercode.core.eligibility.MemoizeAlways;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MemoizeThisInThread {
    String name() default "";
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
