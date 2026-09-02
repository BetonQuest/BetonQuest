package org.betonquest.betonquest.mc_1_20_6;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.item.SimpleQuestItemHandlerRegistry;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedNameHandler;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedPotionHandler;

/**
 * Allows to register features with Minecraft 1.20.6.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_20_6 implements Integration {

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_20_6(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final SimpleQuestItemHandlerRegistry handlerRegistry = betonQuest.getComponentLoader().get(SimpleQuestItemHandlerRegistry.class);
        handlerRegistry.register(new UpdatedNameHandler());
        handlerRegistry.register(new UpdatedPotionHandler());
    }

    @Override
    public void postEnable(final BetonQuestApi betonQuestApi) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
