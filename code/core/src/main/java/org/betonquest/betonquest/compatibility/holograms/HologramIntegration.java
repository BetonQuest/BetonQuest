package org.betonquest.betonquest.compatibility.holograms;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;

/**
 * An Integration that provides a {@link BetonHologramFactory} to create BetonQuest Holograms.
 */
public interface HologramIntegration extends Integration {

    /**
     * Gets the name of the plugin used in the configuration to retrieve its priority.
     *
     * @return the name of the plugin
     */
    String getPluginName();

    /**
     * Gets a HologramCreator from this Integration.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance providing access to BetonQuest's API
     * @return the integration specific hologram factory
     * @throws QuestException if the factory could not be constructed
     */
    BetonHologramFactory getHologramFactory(BetonQuestApi betonQuestApi) throws QuestException;
}
