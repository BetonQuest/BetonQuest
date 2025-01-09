package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.quest.registry.QuestTypeRegistries;
import org.bukkit.Bukkit;

/**
 * Integrator for the Quests plugin.
 */
public class QuestsIntegrator implements Integrator {
    /**
     * The Quests plugin instance.
     */
    @SuppressWarnings("NullAway.Init")
    private static Quests questsInstance;

    /**
     * The BetonQuest plugin instance.
     */
    private final BetonQuest plugin;

    /**
     * The default constructor.
     */
    public QuestsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    /**
     * Gets the used Quests instance.
     *
     * @return the active Quests instance.
     */
    public static Quests getQuestsInstance() {
        return questsInstance;
    }

    @Override
    public void hook() {
        questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.getConditionTypes().register("quest", QuestsCondition.class);
        questRegistries.getEventTypes().register("quest", QuestsEvent.class);

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        questsInstance.getCustomRewards().add(new EventReward(loggerFactory.create(EventReward.class)));
        questsInstance.getCustomRequirements().add(new ConditionRequirement(loggerFactory.create(ConditionRequirement.class)));
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
