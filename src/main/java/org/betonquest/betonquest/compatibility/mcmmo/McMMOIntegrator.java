package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;

/**
 * Integrator for McMMO.
 */
public class McMMOIntegrator implements Integrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public McMMOIntegrator() {
        plugin = BetonQuest.getInstance();
        this.log = plugin.getLoggerFactory().create(getClass());
    }

    @Override
    public void hook() {
        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.condition().register("mcmmolevel", new McMMOSkillLevelConditionFactory(loggerFactory, data));
        questRegistries.event().register("mcmmoexp", new McMMOAddExpEventFactory(loggerFactory, data));
        try {
            server.getPluginManager().registerEvents(new MCMMOQuestItemHandler(plugin.getProfileProvider()), plugin);
            log.debug("Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            log.warn("MCMMO version is not compatible with the QuestItemHandler.", e);
        }
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
