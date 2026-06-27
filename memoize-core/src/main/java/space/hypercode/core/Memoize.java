package space.hypercode.core;

import lombok.Getter;
import space.hypercode.core.configs.MemoizationConfigs;
import space.hypercode.core.converters.ConverterResolver;
import space.hypercode.core.eligibility.EligibilityCriteriaResolver;
import space.hypercode.core.init.MemoizeInitializer;
import space.hypercode.core.metrics.MemoizationMetrics;
import space.hypercode.core.metrics.NoOpMetrics;
import space.hypercode.core.providers.MemoizationProviderFactory;
import space.hypercode.core.utils.Preconditions;


/**
 * Main entry point and singleton for configuring and starting the memoization framework.
 *
 * <p>Use {@link #create()} to obtain a {@link MemoizeBuilder}, configure it, call
 * {@link MemoizeBuilder#build()}, and then {@link #start()} to activate memoization.
 * Only one instance may be active per JVM.
 *
 * <p>Example usage:
 * <pre>{@code
 * Memoize.create()
 *     .scanIn("com.example")
 *     .providerFactory(myFactory)
 *     .build()
 *     .start();
 * }</pre>
 */
public class Memoize {

    private static volatile Memoize INSTANCE;

    /** The root package to scan for {@link space.hypercode.core.annotations.MemoizeThis}-annotated methods. */
    @Getter
    private final String packageName;
    /** Registry of named cache configurations. */
    @Getter
    private final MemoizationConfigs configs;
    /** Factory used to create {@link space.hypercode.core.providers.MemoizationProvider} instances for each named cache. */
    @Getter
    private final MemoizationProviderFactory providerFactory;
    /** Metrics recorder for cache operations. Defaults to {@link NoOpMetrics} if not specified. */
    @Getter
    private final MemoizationMetrics metrics;
    /** Resolver for determining which {@link space.hypercode.core.converters.MemoizationKeyConverter} to use per method call. */
    @Getter
    private final ConverterResolver converterResolver;
    /** Resolver for determining which {@link space.hypercode.core.eligibility.EligibilityCriteria} to use per method call. */
    @Getter
    private final EligibilityCriteriaResolver eligibilityCriteriaResolver;


    private Memoize(final String packageName,
                    final MemoizationConfigs configs,
                    final MemoizationProviderFactory providerFactory,
                    final MemoizationMetrics metrics) {

        this.packageName = Preconditions.validateNotNullOrEmpty(packageName, "packageName can't be null or empty");
        this.configs = configs == null ? new MemoizationConfigs() : configs;
        this.providerFactory = Preconditions.validateNonNull(providerFactory, "providerFactory can't be null");
        this.metrics = metrics == null ? new NoOpMetrics() : metrics;
        this.converterResolver = new ConverterResolver(this.configs);
        this.eligibilityCriteriaResolver = new EligibilityCriteriaResolver(this.configs);
    }

    /**
     * Initializes the singleton, runs validation, and makes memoization active.
     * Throws if already started.
     */
    public void start() {
        synchronized (Memoize.class) {
            if (INSTANCE != null) {
                throw new IllegalStateException("Memoize has already been started. Only one instance is allowed per JVM.");
            }
            INSTANCE = this;
        }
        new MemoizeInitializer(this).initialize();
    }

    /**
     * Returns the singleton instance, or null if not yet started.
     */
    public static Memoize getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new {@link MemoizeBuilder} for configuring a {@link Memoize} instance.
     *
     * @return a new builder
     */
    public static MemoizeBuilder create() {
        return new MemoizeBuilder();
    }

    /**
     * Builder for constructing a {@link Memoize} instance with the desired configuration.
     */
    public static class MemoizeBuilder {
        private String packageName;
        private MemoizationConfigs configs;
        private MemoizationProviderFactory providerFactory;
        private MemoizationMetrics metrics;

        private MemoizeBuilder() {
            // to prevent initialization
        }

        /**
         * Sets the root package to scan for {@link space.hypercode.core.annotations.MemoizeThis}-annotated methods.
         *
         * @param packageName the package name to scan (must not be null or empty)
         * @return this builder
         * @throws IllegalArgumentException if {@code packageName} is null or empty
         */
        public MemoizeBuilder scanIn(final String packageName) {
            this.packageName = Preconditions.validateNotNullOrEmpty(packageName, "packageName can't be null or empty");
            return this;
        }

        /**
         * Sets the named cache configurations registry.
         *
         * @param memoizationConfigs the configurations (must not be null)
         * @return this builder
         * @throws IllegalArgumentException if {@code memoizationConfigs} is null
         */
        public MemoizeBuilder configs(final MemoizationConfigs memoizationConfigs) {
            this.configs = Preconditions.validateNonNull(memoizationConfigs, "configs can't be null");
            return this;
        }

        /**
         * Sets the factory used to create cache provider instances.
         *
         * @param providerFactory the provider factory (must not be null)
         * @return this builder
         * @throws IllegalArgumentException if {@code providerFactory} is null
         */
        public MemoizeBuilder providerFactory(final MemoizationProviderFactory providerFactory) {
            this.providerFactory = Preconditions.validateNonNull(providerFactory, "providerFactory can't be null");
            return this;
        }

        /**
         * Sets the metrics recorder for cache operations.
         * If not set, a {@link NoOpMetrics} instance is used.
         *
         * @param metrics the metrics implementation, or {@code null} for no-op
         * @return this builder
         */
        public MemoizeBuilder metrics(final MemoizationMetrics metrics) {
            this.metrics = metrics;
            return this;
        }

        /**
         * Builds a new {@link Memoize} instance with the configured settings.
         * Call {@link Memoize#start()} on the returned instance to activate memoization.
         *
         * @return a new {@link Memoize} instance
         */
        public Memoize build() {
            return new Memoize(packageName, configs, providerFactory, metrics);
        }

    }
}
