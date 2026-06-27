package space.hypercode.core.configs;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe registry of named {@link MemoizationConfig} instances.
 *
 * <p>Configurations are registered by name and looked up at runtime when a
 * {@link space.hypercode.core.annotations.MemoizeThis}-annotated method has
 * {@code useConfig=true}.
 */
public class MemoizationConfigs {
    private final Map<String, MemoizationConfig> configs;

    /**
     * Creates an empty configuration registry.
     */
    public MemoizationConfigs() {
        this.configs = new ConcurrentHashMap<>();
    }

    /**
     * Registers a cache configuration under the given name.
     * Overwrites any previously registered configuration with the same name.
     *
     * @param name              the cache name (used as the lookup key)
     * @param memoizationConfig the configuration to register
     */
    public void add(final String name, final MemoizationConfig memoizationConfig) {
        this.configs.put(name, memoizationConfig);
    }

    /**
     * Retrieves the configuration registered under the given name.
     *
     * @param name the cache name to look up
     * @return an {@link Optional} containing the configuration, or empty if not registered
     */
    public Optional<MemoizationConfig> get(final String name) {
        return Optional.ofNullable(
                configs.get(name)
        );
    }
}
