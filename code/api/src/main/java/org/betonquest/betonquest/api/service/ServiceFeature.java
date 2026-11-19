package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.jetbrains.annotations.Contract;

/**
 * A service feature provides access to a manager and a registry for a specific quest feature type.
 * <br><br>
 * The registry is responsible for registering custom features.
 * Registries typically implement {@link CoreQuestRegistry}, {@link FeatureTypeRegistry},
 * or {@link FeatureRegistry} and provide various methods for registering custom features with their factories.
 * <br><br>
 * The manager is responsible for providing access to existing and loaded feature types
 * that were previously registered with the registry.
 *
 * @param <M> the type of the manager
 * @param <R> the type of the registry
 * @since 3.0.0
 */
public interface ServiceFeature<M, R> {

    /**
     * Returns the manager for this feature.
     *
     * @return the manager instance
     * @since 3.0.0
     */
    @Contract(pure = true)
    M manager();

    /**
     * Returns the registry for this feature.
     *
     * @return the registry instance
     * @since 3.0.0
     */
    @Contract(pure = true)
    R registry();
}
