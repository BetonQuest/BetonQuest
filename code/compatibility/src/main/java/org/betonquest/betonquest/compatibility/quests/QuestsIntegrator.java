package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Integrator for the Quests plugin.
 */
public class QuestsIntegrator implements Integration {

    /**
     * The default constructor.
     */
    public QuestsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Quests");
        final Quests questsInstance = (Quests) plugin;
        Objects.requireNonNull(questsInstance);

        api.conditions().registry().register("quest", new QuestsConditionFactory(questsInstance));
        api.actions().registry().register("quest", new QuestsActionFactory(questsInstance));

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final ProfileProvider profileProvider = api.profiles();
        final IdentifierFactory<ActionIdentifier> actionIdentifierFactory =
                api.identifiers().getFactory(ActionIdentifier.class);
        questsInstance.getCustomRewards().add(new ActionReward(
                loggerFactory.create(ActionReward.class),
                api.actions().manager(), profileProvider, actionIdentifierFactory));
        final IdentifierFactory<ConditionIdentifier> conditionIdentifierFactory =
                api.identifiers().getFactory(ConditionIdentifier.class);
        questsInstance.getCustomRequirements().add(new ConditionRequirement(
                loggerFactory.create(ConditionRequirement.class),
                api.conditions().manager(), profileProvider, conditionIdentifierFactory));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
