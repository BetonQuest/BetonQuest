package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.bukkit.plugin.Plugin;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integration {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "FancyNpcs";

    /**
     * The plugin instance to run tasks on.
     */
    private final Plugin plugin;

    /**
     * The empty default Constructor.
     *
     * @param plugin the plugin instance to run tasks on
     */
    public FancyNpcsIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        api.bukkit().registerEvents(new FancyCatcher(plugin, profileProvider, npcRegistry));
        api.bukkit().registerEvents(new FancyHider(plugin, api.npcs().manager()));
        npcRegistry.register(PREFIX, new FancyFactory(plugin));
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
