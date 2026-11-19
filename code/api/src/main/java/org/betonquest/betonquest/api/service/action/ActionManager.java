package org.betonquest.betonquest.api.service.action;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * The ActionManager is responsible for executing actions loaded by BetonQuest.
 * <br><br>
 * Each action is uniquely identified by an {@link ActionIdentifier}, which consists of the user-defined name
 * in the configuration and the {@link QuestPackage} it belongs to.
 * <br><br>
 * Executing an action runs the logic specified in the action's instruction along with any side effects.
 * For example, executing an action that defines a folder of actions will run all actions within that folder,
 * which is considered a side effect of the original action.
 *
 * @since 3.0.0
 */
public interface ActionManager {

    /**
     * Executes an action for the optionally specified {@link Profile} if its conditions are met.
     * <br><br>
     * The specified profile is used to resolve placeholders in the action's instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the execution to fail.
     *
     * @param profile          the profile to execute the action for, or {@code null} if no profile is involved
     * @param actionIdentifier the identifier of the action to execute
     * @return {@code true} if the action was successfully executed,
     * {@code false} if it does not exist or its conditions were not met
     * @since 3.0.0
     */
    boolean run(@Nullable Profile profile, ActionIdentifier actionIdentifier);

    /**
     * Executes multiple actions for the optionally specified {@link Profile}.
     * Only actions whose conditions are met will be executed.
     * <br><br>
     * The order of execution is not guaranteed, although executing a {@link Collection} typically retains
     * the order if one is present. The most common reason for order deviation is synchronization
     * requiring a wait for the server's main thread.
     * <br><br>
     * The specified profile is used to resolve placeholders in each action's instructions and any side effects.
     * If no profile is specified, placeholders requiring one will cause the execution to fail.
     *
     * @param profile           the profile to execute the actions for, or {@code null} if no profile is involved
     * @param actionIdentifiers the identifiers of the actions to execute
     * @return {@code true} if all actions were successfully executed,
     * {@code false} if any action does not exist or its conditions were not met
     * @since 3.0.0
     */
    boolean run(@Nullable Profile profile, Collection<ActionIdentifier> actionIdentifiers);
}
