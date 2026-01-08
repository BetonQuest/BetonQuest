package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the arguments defined per objective that may be parsed independently of their implementation.
 *
 * @param conditions     the optional conditions that must be met to receive forwarded bukkit actions
 * @param actions        the optional actions that will be called after completing the objective
 * @param persistent     the boolean flag to decide if the objective will be reapplied after completing
 * @param notifyInterval the number flag to determine how often the objective should inform about progression
 */
public record ObjectiveServiceData(Optional<Argument<List<ConditionID>>> conditions,
                                   Optional<Argument<List<ActionID>>> actions,
                                   FlagArgument<Boolean> persistent, FlagArgument<Number> notifyInterval)
        implements ObjectiveServiceDataProvider {

    @Override
    public boolean isPersistent(@Nullable final Profile profile) throws QuestException {
        return persistent.getValue(profile).orElse(false);
    }

    @Override
    public List<ActionID> getActions(@Nullable final Profile profile) throws QuestException {
        if (actions.isPresent()) {
            return actions.get().getValue(profile);
        }
        return Collections.emptyList();
    }

    @Override
    public List<ConditionID> getConditions(@Nullable final Profile profile) throws QuestException {
        if (conditions.isPresent()) {
            return conditions.get().getValue(profile);
        }
        return Collections.emptyList();
    }

    @Override
    public int getNotificationInterval(@Nullable final Profile profile) throws QuestException {
        return notifyInterval.getValue(profile).orElse(0).intValue();
    }
}
