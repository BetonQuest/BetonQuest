package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.jetbrains.annotations.Contract;

/**
 * A service feature offers access to a manager and registry for a specific feature type.
 * <br> <br>
 * The registry is responsible for registering custom features.
 * Registries usually implement either the {@link CoreQuestRegistry}, the {@link FeatureTypeRegistry},
 * or the {@link FeatureRegistry} interfaces and offer varying methods for registering custom features with their
 * factories.
 * The manager is responsible for granting access to existing and loaded types
 * previously registered with the registry.
 *
 * @param <M> the manager type
 * @param <R> the registry type
 * @since 3.0.0
 */
public interface ServiceFeature<M, R> {

    /**
     * Get the manager for this feature.
     *
     * @return the manager instance
     * @since 3.0.0
     */
    @Contract(pure = true)
    M manager();

    /**
     * Get the registry for this feature.
     *
     * @return the registry instance
     * @since 3.0.0
     */
    @Contract(pure = true)
    R registry();
}
