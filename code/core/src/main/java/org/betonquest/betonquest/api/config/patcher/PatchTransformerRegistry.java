package org.betonquest.betonquest.api.config.patcher;

import java.util.Map;

/**
 * Registry for patch transformers.
 */
@FunctionalInterface
public interface PatchTransformerRegistry {

    /**
     * Get a Map of {@link PatchTransformer}s and their names as keys.
     *
     * @return a Map of transformers
     */
    Map<String, PatchTransformer> getTransformers();
}
