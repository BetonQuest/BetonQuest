package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ObjectiveID;
import org.betonquest.betonquest.kernel.processor.CoreQuestRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * The Core Quest Type logic.
 */
public final class QuestTypeApi {

    /**
     * Quest Registry providing processors.
     */
    private final CoreQuestRegistry questRegistry;

    /**
     * Create a new Quest API.
     *
     * @param questRegistry the registry containing processors
     */
    public QuestTypeApi(final CoreQuestRegistry questRegistry) {
        this.questRegistry = questRegistry;
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    public boolean conditions(@Nullable final Profile profile, final Collection<ConditionID> conditionIDs) {
        final ConditionID[] ids = new ConditionID[conditionIDs.size()];
        int index = 0;
        for (final ConditionID id : conditionIDs) {
            ids[index++] = id;
        }
        return conditions(profile, ids);
    }

    /**
     * Checks if the conditions described by conditionID are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    public boolean conditions(@Nullable final Profile profile, final ConditionID... conditionIDs) {
        return questRegistry.conditions().checks(profile, conditionIDs);
    }

    /**
     * Checks if the condition described by conditionID is met.
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    public boolean condition(@Nullable final Profile profile, final ConditionID conditionID) {
        return questRegistry.conditions().check(profile, conditionID);
    }

    /**
     * Fires an event for the {@link Profile} if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} for which the event must be executed or null
     * @param eventID ID of the event to fire
     * @return true if the event was run even if there was an exception during execution
     */
    public boolean event(@Nullable final Profile profile, final EventID eventID) {
        return questRegistry.events().execute(profile, eventID);
    }

    /**
     * Creates new objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    public void newObjective(final Profile profile, final ObjectiveID objectiveID) {
        questRegistry.objectives().start(profile, objectiveID);
    }

    /**
     * Resumes the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    public void resumeObjective(final Profile profile, final ObjectiveID objectiveID, final String instruction) {
        questRegistry.objectives().resume(profile, objectiveID, instruction);
    }

    /**
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameObjective(final ObjectiveID name, final ObjectiveID rename) {
        questRegistry.objectives().renameObjective(name, rename);
    }

    /**
     * Returns the list of objectives of this player.
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    public List<Objective> getPlayerObjectives(final Profile profile) {
        return questRegistry.objectives().getActive(profile);
    }

    /**
     * Gets a stored Objective.
     *
     * @param objectiveID the id of the objective
     * @return the loaded Objective
     * @throws QuestException if no Objective is loaded for the ID
     */
    public Objective getObjective(final ObjectiveID objectiveID) throws QuestException {
        return questRegistry.objectives().get(objectiveID);
    }
}
