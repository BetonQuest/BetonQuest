package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.QuestTypeRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;

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
    public void hook(final BetonQuestApi api) {
        final Quests questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        Objects.requireNonNull(questsInstance);

        final PrimaryServerThreadData data = api.getPrimaryServerThreadData();

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("quest", new QuestsConditionFactory(questsInstance, data));
        questRegistries.event().register("quest", new QuestsEventFactory(questsInstance, data));

        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final QuestTypeApi questTypeApi = api.getQuestTypeApi();
        final ProfileProvider profileProvider = plugin.getProfileProvider();
        questsInstance.getCustomRewards().add(new EventReward(
                loggerFactory.create(EventReward.class), api.getQuestPackageManager(), questTypeApi, profileProvider));
        questsInstance.getCustomRequirements().add(new ConditionRequirement(
                loggerFactory.create(ConditionRequirement.class), api.getQuestPackageManager(), questTypeApi, profileProvider));
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
