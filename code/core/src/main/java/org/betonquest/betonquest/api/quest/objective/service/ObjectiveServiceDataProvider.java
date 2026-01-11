package org.betonquest.betonquest.api.quest.objective.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Defines additional information accessible per objective.
 */
public interface ObjectiveServiceDataProvider {

    /**
     * If the objective is persistent.
     * <br>
     * A persistent objective will be reapplied after completion.
     *
     * @param profile the profile to check for
     * @return if the objective is persistent
     * @throws QuestException if argument resolving failed
     */
    boolean isPersistent(@Nullable Profile profile) throws QuestException;

    /**
     * Retrieves a list of actionIds for actions to call after completing the objective.
     *
     * @param profile the profile to get the actions for
     * @return a list of actionIds
     * @throws QuestException if argument resolving failed
     */
    List<ActionIdentifier> getActions(@Nullable Profile profile) throws QuestException;

    /**
     * Retrieves a list of conditionIds for conditions to check before each bukkit event for the objective.
     *
     * @param profile the profile to get the conditions for
     * @return a list of conditionIds
     * @throws QuestException if argument resolving failed
     */
    List<ConditionIdentifier> getConditions(@Nullable Profile profile) throws QuestException;

    /**
     * Retrieves the notification interval for the objective.
     * An interval of 0 disables notifications.
     *
     * @param profile the profile to get the interval for
     * @return the notification interval
     * @throws QuestException if argument resolving failed
     */
    int getNotificationInterval(@Nullable Profile profile) throws QuestException;
}
