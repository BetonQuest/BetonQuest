package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Registers the Conditions, Events, Objectives and Variables that come with BetonQuest.
 */
public class CoreQuestTypes {
    /**
     * Logger Factory to create new custom Logger from.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server used for primary server thread access.
     */
    private final Server server;

    /**
     * Scheduler used for primary server thread access.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin used for primary server thread access, type registration and general usage.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory used in event factories
     * @param server        the server used for primary server thread access.
     * @param scheduler     used in event factories
     * @param plugin        used in event factories and for objective type registration
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Registers the Quest Types.
     */
    public void register() {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions();
        registerEvents();
        registerObjectives();
        registerVariables();
    }

    private void registerConditions() {
    }

    private void registerEvents() {
    }

    private void registerObjectives() {
    }

    private void registerVariables() {
    }
}
