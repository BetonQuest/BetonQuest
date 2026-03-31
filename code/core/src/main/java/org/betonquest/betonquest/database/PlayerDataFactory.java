package org.betonquest.betonquest.database;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.feature.journal.JournalFactory;
import org.bukkit.Server;

import java.util.function.Supplier;

/**
 * Factory to create PlayerData objects for profiles.
 */
public class PlayerDataFactory {

    /**
     * Factory to create new class-specific loggers.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Saver to persist player data changes.
     */
    private final Saver saver;

    /**
     * The server to determine if an event should be async.
     */
    private final Server server;

    /**
     * The database connector.
     */
    private final Connector connector;

    /**
     * Identifier registry to resolve identifiers.
     */
    private final Identifiers identifierRegistry;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The config accessor to get the plugin config.
     */
    private final ConfigAccessor config;

    /**
     * Factory to create a new Journal.
     */
    private final Supplier<JournalFactory> journalFactory;

    /**
     * Create a new Player Data Factory.
     *
     * @param loggerFactory      the logger factory to create class-specific logger
     * @param saver              the saver to persist data changes
     * @param server             the server to determine if an event should be stated as async
     * @param connector          the database connector to use
     * @param identifierRegistry the identifier registry to resolve identifiers
     * @param objectiveManager   the objective manager
     * @param config             the config accessor to get the plugin config
     * @param journalFactory     the supplier for the journal factory to use
     */
    public PlayerDataFactory(final BetonQuestLoggerFactory loggerFactory, final Saver saver, final Server server,
                             final Connector connector, final Identifiers identifierRegistry,
                             final ObjectiveManager objectiveManager, final ConfigAccessor config,
                             final Supplier<JournalFactory> journalFactory) {
        this.connector = connector;
        this.identifierRegistry = identifierRegistry;
        this.loggerFactory = loggerFactory;
        this.saver = saver;
        this.server = server;
        this.objectiveManager = objectiveManager;
        this.config = config;
        this.journalFactory = journalFactory;
    }

    /**
     * Create a new PlayerData.
     *
     * @param profile the profile to create the player data for
     * @return the newly created player data
     */
    public PlayerData createPlayerData(final Profile profile) {
        return new PlayerData(loggerFactory.create(PlayerData.class), saver, server, connector, identifierRegistry,
                objectiveManager, config, journalFactory.get(), profile);
    }
}
