package org.betonquest.betonquest.compatibility.denizen;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.denizen.event.DenizenTaskScriptEventFactory;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrator for Denizen.
 */
public class DenizenIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public DenizenIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        plugin.getQuestRegistries().event().register("script", new DenizenTaskScriptEventFactory(data));
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
