package org.betonquest.betonquest.api.config.patcher;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Interface for transformers that transform a configuration.
 */
@FunctionalInterface
public interface PatchTransformer {

    /**
     * Applies a transformer to the given config.
     *
     * @param options options for the transformer
     * @param config  to transform
     * @throws PatchException if the transformation failed
     */
    void transform(Map<String, String> options, ConfigurationSection config) throws PatchException;
}
