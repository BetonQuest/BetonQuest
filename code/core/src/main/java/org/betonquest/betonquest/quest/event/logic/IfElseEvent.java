package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * The if-else action. Either execute the one or the other action, depending on the condition.
 */
public class IfElseEvent implements NullableEvent {

    /**
     * The condition to check.
     */
    private final Argument<ConditionID> condition;

    /**
     * The action to run if the condition is true.
     */
    private final Argument<ActionID> action;

    /**
     * The action to run if the condition is false.
     */
    private final Argument<ActionID> elseAction;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates a new if-else action.
     *
     * @param condition    the condition to check
     * @param action       the action to run if the condition is true
     * @param elseAction   the action to run if the condition is false
     * @param questTypeApi the Quest Type API
     */
    public IfElseEvent(final Argument<ConditionID> condition, final Argument<ActionID> action, final Argument<ActionID> elseAction, final QuestTypeApi questTypeApi) {
        this.condition = condition;
        this.action = action;
        this.elseAction = elseAction;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        if (questTypeApi.condition(profile, condition.getValue(profile))) {
            questTypeApi.action(profile, action.getValue(profile));
        } else {
            questTypeApi.action(profile, elseAction.getValue(profile));
        }
    }
}
