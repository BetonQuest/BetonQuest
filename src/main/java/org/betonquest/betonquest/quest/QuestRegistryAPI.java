package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.QuestCanceler;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.quest.registry.QuestRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The implementation of the BetonQuestAPI with Processors.
 */
public class QuestRegistryAPI implements QuestTypeAPI, FeatureAPI {
    /**
     * Quest Registry for containing processors.
     */
    private final QuestRegistry questRegistry;

    /**
     * Create a new BetonQuest API.
     *
     * @param questRegistry the registry containing processors
     */
    public QuestRegistryAPI(final QuestRegistry questRegistry) {
        this.questRegistry = questRegistry;
    }

    @Override
    public boolean conditions(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        return questRegistry.conditions().checks(profile, conditionIDs);
    }

    @Override
    public boolean condition(@Nullable final Profile profile, final ConditionID conditionID) {
        return questRegistry.conditions().check(profile, conditionID);
    }

    @Override
    public boolean event(@Nullable final Profile profile, final EventID eventID) {
        return questRegistry.events().execute(profile, eventID);
    }

    @Override
    public void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        questRegistry.objectives().start(profile, objectiveID);
    }

    @Override
    public void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        questRegistry.objectives().resume(profile, objectiveID, instruction);
    }

    @Override
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        questRegistry.objectives().renameObjective(name, rename);
    }

    @Override
    public List<Objective> getPlayerObjectives(final Profile profile) {
        return questRegistry.objectives().getActive(profile);
    }

    @Override
    @Nullable
    public Objective getObjective(final ObjectiveID objectiveID) {
        return questRegistry.objectives().getObjective(objectiveID);
    }

    @Override
    @Nullable
    public ConversationData getConversation(final ConversationID conversationID) {
        return questRegistry.conversations().getConversation(conversationID);
    }

    @Override
    public Map<QuestCancelerID, QuestCanceler> getCanceler() {
        return questRegistry.questCanceller().getCancelers();
    }
}
