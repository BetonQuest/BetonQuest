package org.betonquest.betonquest.compatibility.npc.fancynpcs;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Integrator implementation for the FancyNpcs plugin.
 */
public class FancyNpcsIntegrator implements Integrator {

    /**
     * The prefix used before any registered name for distinguishing.
     */
    public static final String PREFIX = "FancyNpcs";

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * The empty default Constructor.
     *
     * @param plugin the plugin to register listener with
     */
    public FancyNpcsIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final NpcRegistry npcRegistry = api.npcs().registry();
        final ProfileProvider profileProvider = api.profiles();
        final PluginManager manager = plugin.getServer().getPluginManager();
        manager.registerEvents(new FancyCatcher(profileProvider, npcRegistry), plugin);
        final FancyHider hider = new FancyHider(BetonQuest.getInstance().getComponentLoader().get(NpcProcessor.class).getNpcHider());
        manager.registerEvents(hider, plugin);
        npcRegistry.register(PREFIX, new FancyFactory());
        npcRegistry.registerIdentifier(new FancyIdentifier(PREFIX));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
