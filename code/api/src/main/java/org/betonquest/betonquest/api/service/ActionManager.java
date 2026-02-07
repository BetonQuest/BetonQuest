package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The ActionManager is responsible for executing actions that are loaded by BetonQuest.
 * <br> <br>
 * Each action is uniquely identified by an {@link ActionIdentifier} which consists of the user-defined name in the
 * configuration as well as the {@link QuestPackage} the action belongs to.
 * Executing an action will run anything specified in the action's instruction as well as any side effects of that.
 * For example, executing an action defining a folder of actions will run all actions in that folder,
 * which is considered a side effect of the original action itself.
 */
public interface ActionManager {

    /**
     * Executes an action for the optionally specified {@link Profile} if its conditions were fulfilled,
     * or it doesn't define any conditions.
     * <br> <br>
     * The specified profile will be used to resolve any placeholders in the action's instructions as well as in any
     * placeholders contained in invocations of the action's side effects.
     * <br> <br>
     * If no profile is specified, the action will be executed without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the execution will fail.
     *
     * @param profile          the profile to execute the action for or null if no profile is involved
     * @param actionIdentifier the identifier of the action to be called
     * @return false if the action failed to due to non-existence or unfulfilled conditions, true otherwise
     */
    boolean run(@Nullable Profile profile, ActionIdentifier actionIdentifier);

    /**
     * Executes multiple actions for the optionally specified {@link Profile}.
     * Only executes the subset of actions whose conditions were fulfilled or aren't defined.
     * <br> <br>
     * The order of execution is not guaranteed, however executing a {@link Collection} <i>usually</i> retains the order
     * if one is present.
     * The most common reason for breaking the order is synchronization requiring to wait for the servers main thread.
     * <br> <br>
     * The specified profile will be used to resolve any placeholders in each action's instructions as well as in any
     * placeholders contained in invocations of each action's side effects.
     * <br> <br>
     * If no profile is specified, the actions will be executed without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the execution will fail.
     *
     * @param profile           the profile to execute the actions for or null if no profile is involved
     * @param actionIdentifiers the identifiers of the actions to be called
     * @return false if any action failed to due to non-existence or unfulfilled conditions, true otherwise
     */
    boolean run(@Nullable Profile profile, Collection<ActionIdentifier> actionIdentifiers);
}
