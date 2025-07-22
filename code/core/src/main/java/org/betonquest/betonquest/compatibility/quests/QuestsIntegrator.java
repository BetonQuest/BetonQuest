package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.Objects;

/**
 * Integrator for the Quests plugin.
 */
public class QuestsIntegrator implements Integrator {

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

    @Override
    public void hook() {
        final Quests questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        Objects.requireNonNull(questsInstance);

        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);

        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        questRegistries.condition().register("quest", new QuestsConditionFactory(questsInstance, data));
        questRegistries.event().register("quest", new QuestsEventFactory(questsInstance, data));

        final BetonQuestLoggerFactory loggerFactory = plugin.getLoggerFactory();
        final QuestTypeAPI questTypeAPI = plugin.getQuestTypeAPI();
        final ProfileProvider profileProvider = plugin.getProfileProvider();
        questsInstance.getCustomRewards().add(new EventReward(
                loggerFactory.create(EventReward.class), questTypeAPI, profileProvider));
        questsInstance.getCustomRequirements().add(new ConditionRequirement(
                loggerFactory.create(ConditionRequirement.class), questTypeAPI, profileProvider));
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
