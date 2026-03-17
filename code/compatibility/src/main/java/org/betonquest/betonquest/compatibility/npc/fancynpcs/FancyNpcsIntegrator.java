package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integration {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "FancyNpcs";

    /**
     * The empty default Constructor.
     */
    public FancyNpcsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new FancyCatcher(profileProvider, npcRegistry));
        final FancyHider hider = new FancyHider(BetonQuest.getInstance().getComponentLoader().get(NpcProcessor.class).getNpcHider());
        api.bukkit().registerEvents(hider);
        npcRegistry.register(PREFIX, new FancyFactory());
        npcRegistry.registerIdentifier(new FancyIdentifier(PREFIX));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
