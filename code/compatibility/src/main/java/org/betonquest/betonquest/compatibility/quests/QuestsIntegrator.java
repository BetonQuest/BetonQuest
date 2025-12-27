package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.Placeholders;
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
     * The default constructor.
     */
    public QuestsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final Quests questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        Objects.requireNonNull(questsInstance);

        final QuestTypeRegistries questRegistries = api.getQuestRegistries();
        questRegistries.condition().register("quest", new QuestsConditionFactory(questsInstance));
        questRegistries.event().register("quest", new QuestsEventFactory(questsInstance));

        final BetonQuestLoggerFactory loggerFactory = api.getLoggerFactory();
        final QuestTypeApi questTypeApi = api.getQuestTypeApi();
        final ProfileProvider profileProvider = api.getProfileProvider();
        final Placeholders placeholders = api.getQuestTypeApi().placeholders();
        questsInstance.getCustomRewards().add(new EventReward(
                loggerFactory.create(EventReward.class), placeholders, api.getQuestPackageManager(), questTypeApi, profileProvider));
        questsInstance.getCustomRequirements().add(new ConditionRequirement(
                loggerFactory.create(ConditionRequirement.class), placeholders, api.getQuestPackageManager(), questTypeApi, profileProvider));
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
