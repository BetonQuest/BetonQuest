package org.betonquest.betonquest;

import org.betonquest.betonquest.api.BetonQuestAPI;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.quest.registry.QuestRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the BetonQuestAPI with Processors.
 */
public class APIImpl implements BetonQuestAPI {
    /**
     * Quest Registry for containing processors.
     */
    private final QuestRegistry questRegistry;

    /**
     * Create a new BetonQuest API.
     *
     * @param questRegistry the registry containing processors
     */
    public APIImpl(final QuestRegistry questRegistry) {
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
}
