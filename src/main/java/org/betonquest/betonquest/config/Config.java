package org.betonquest.betonquest.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;

import java.util.Map;

/**
 * Handles the configuration of the plugin.
 */
@SuppressWarnings("NullAway.Init")
public final class Config {
    /**
     * The quest manager.
     */
    private static QuestManager questManager;

    /**
     * The plugin instance.
     */
    private static BetonQuest plugin;

    private Config() {
    }

    /**
     * Creates new instance of the Config handler.
     *
     * @param plugin the {@link BetonQuest} plugin instance
     */
    @SuppressFBWarnings("EI_EXPOSE_STATIC_REP2")
    public static void setup(final BetonQuest plugin) {
        Config.plugin = plugin;
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        questManager = new QuestManager(loggerFactory, loggerFactory.create(QuestManager.class), plugin.getConfigAccessorFactory(), plugin.getDataFolder());
    }

    /**
     * Get all the packages loaded by the plugin.
     *
     * @return the map of packages and their names
     */
    public static Map<String, QuestPackage> getPackages() {
        return questManager.getPackages();
    }
}
