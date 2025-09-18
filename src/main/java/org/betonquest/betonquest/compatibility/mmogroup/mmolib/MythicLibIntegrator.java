package org.betonquest.betonquest.compatibility.mmogroup.mmolib;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrates MythicLib.
 * MythicLib not affiliated with MythicCraft. It is part of Phoenix developments' MMO plugin suite.
 */
public class MythicLibIntegrator implements Integrator {

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * Creates a new MythicLib integrator.
     */
    public MythicLibIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        plugin.getQuestRegistries().condition().register("mmostat", new MythicLibStatConditionFactory(data));

        plugin.getQuestRegistries().objective().register("mmoskill", new MythicLibSkillObjectiveFactory());
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
