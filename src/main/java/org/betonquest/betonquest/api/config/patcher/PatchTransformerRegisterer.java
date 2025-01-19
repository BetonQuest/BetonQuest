package org.betonquest.betonquest.api.config.patcher;

import org.betonquest.betonquest.config.Patcher;

/**
 * Functional interface for registering all transformers of a {@link Patcher}.
 */
public interface PatchTransformerRegisterer {

    /**
     * Functional interface for registering all transformers of a {@link Patcher}.
     *
     * @param patcher the {@link Patcher} to register the transformers for
     */
    void registerTransformers(Patcher patcher);
}

