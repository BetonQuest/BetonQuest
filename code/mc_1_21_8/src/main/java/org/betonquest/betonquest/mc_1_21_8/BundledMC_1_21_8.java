package org.betonquest.betonquest.mc_1_21_8;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.dependency.CoreComponentLoader;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.mc_1_21_8.conversation.io.DialogConvIOFactory;

/**
 * Allows to register features with Minecraft 1.21.8.
 */
@SuppressWarnings("PMD.ClassNamingConventions")
public class BundledMC_1_21_8 implements Integration {

    /**
     * BetonQuest class to get relevant object from.
     */
    private final BetonQuest betonQuest;

    /**
     * Creates a new Object to register Minecraft version changes.
     *
     * @param betonQuest the BetonQuest class to get relevant object from
     */
    public BundledMC_1_21_8(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final CoreComponentLoader componentLoader = betonQuest.getComponentLoader();
        final FontRegistry fontRegistry = componentLoader.get(FontRegistry.class);

        componentLoader.get(ConversationIORegistry.class).register("dialog", new DialogConvIOFactory(
                betonQuest.getPluginConfig(),
                componentLoader.get(ConversationColors.class),
                new ComponentLineWrapper(fontRegistry)
        ));
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
