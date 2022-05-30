package org.betonquest.betonquest.modules.config.transformer;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

/**
 * Interface for transformers that transform a configuration.
 */
public interface PatchTransformation {

    /**
     * Applies a transformation to the given config.
     *
     * @param options options for the transformer
     * @param config  to transform
     */
    void transform(Map<String, String> options, ConfigurationSection config);
}
