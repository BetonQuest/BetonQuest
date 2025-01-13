package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.bukkit.Bukkit;

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
        final QuestTypeRegistries questRegistries = BetonQuest.getInstance().getQuestRegistries();
        questRegistries.condition().register("mcmmolevel", McMMOSkillLevelCondition.class);
        questRegistries.event().register("mcmmoexp", McMMOAddExpEvent.class);
        try {
            Bukkit.getPluginManager().registerEvents(new MCMMOQuestItemHandler(), plugin);
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
