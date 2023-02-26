package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Server;

/**
 * Integrates with FakeBlock
 */
public class FakeBlockApiIntegrator implements Integrator {
    /**
     * The instance of {@link BetonQuest}.
     */
    private final BetonQuest plugin;

    /**
     * Create the FakeBlock integration.
     */
    public FakeBlockApiIntegrator() {
        this.plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        final Server server = plugin.getServer();
        plugin.registerNonStaticEvent("fakeblock", new FakeBlockEventFactory(server, server.getScheduler(), plugin));
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
