package org.betonquest.betonquest.api.config.patcher;

import java.util.Map;

/**
 * Registry for patch transformers.
 */
public interface PatchTransformerRegistry {

    /**
     * Get a Map of transformers to use for patching and their names.
     *
     * @return a Map of transformers
     */
    Map<String, PatchTransformer> getTransformers();
}
