package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveID;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * The QuestTypeApi provides access to the quest related core features.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface QuestTypeApi {

    /**
     * Checks whether all the conditions described by conditionIDs are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    boolean conditions(@Nullable Profile profile, Collection<ConditionID> conditionIDs);

    /**
     * Checks whether any of the conditions described by conditionIDs are met.
     *
     * @param profile      the {@link Profile} of the player which should be checked
     * @param conditionIDs IDs of the conditions to check
     * @return if all conditions are met
     */
    boolean conditionsAny(@Nullable Profile profile, Collection<ConditionID> conditionIDs);

    /**
     * Checks if the condition described by conditionID is met.
     *
     * @param conditionID ID of the condition to check
     * @param profile     the {@link Profile} of the player which should be checked
     * @return if the condition is met
     */
    boolean condition(@Nullable Profile profile, ConditionID conditionID);

    /**
     * Fires actions for the {@link Profile} if it meets the actions' conditions.
     * If the profile is null, the actions will be fired as static actions.
     *
     * @param profile   the {@link Profile} for which the actions must be executed or null
     * @param actionIDS IDs of the actions to fire
     * @return true if all actions were run even if there were exceptions during execution
     */
    boolean actions(@Nullable Profile profile, Collection<ActionID> actionIDS);

    /**
     * Fires an action for the {@link Profile} if it meets the action's conditions.
     * If the profile is null, the action will be fired as a static action.
     *
     * @param profile  the {@link Profile} for which the action must be executed or null
     * @param actionID ID of the action to fire
     * @return true if the action was run even if there was an exception during execution
     */
    boolean action(@Nullable Profile profile, ActionID actionID);

    /**
     * Creates new objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    void newObjective(Profile profile, ObjectiveID objectiveID);

    /**
     * Pauses the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    void pauseObjective(Profile profile, ObjectiveID objectiveID);

    /**
     * Cancels the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     */
    void cancelObjective(Profile profile, ObjectiveID objectiveID);

    /**
     * Resumes the existing objective for given player.
     *
     * @param profile     the {@link Profile} of the player
     * @param objectiveID ID of the objective
     * @param instruction data instruction string
     */
    void resumeObjective(Profile profile, ObjectiveID objectiveID, String instruction);

    /**
     * Renames the objective instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    void renameObjective(ObjectiveID name, ObjectiveID rename);

    /**
     * Returns the list of objectives of this player.
     *
     * @param profile the {@link Profile} of the player
     * @return list of this player's active objectives
     */
    List<DefaultObjective> getPlayerObjectives(Profile profile);

    /**
     * Gets a stored Objective.
     *
     * @param objectiveID the id of the objective
     * @return the loaded Objective
     * @throws QuestException if no Objective is loaded for the ID
     */
    DefaultObjective getObjective(ObjectiveID objectiveID) throws QuestException;

    /**
     * Get the Api for placeholder interaction.
     *
     * @return placeholder api
     */
    Placeholders placeholders();
}
