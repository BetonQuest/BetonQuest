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
 * The if-else event. Either execute the one or the other event, depending on the condition.
 */
public class IfElseEvent implements NullableEvent {

    /**
     * The condition to check.
     */
    private final Argument<ConditionID> condition;

    /**
     * The event to run if the condition is true.
     */
    private final Argument<ActionID> event;

    /**
     * The event to run if the condition is false.
     */
    private final Argument<ActionID> elseEvent;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Creates a new if-else event.
     *
     * @param condition    the condition to check
     * @param event        the event to run if the condition is true
     * @param elseEvent    the event to run if the condition is false
     * @param questTypeApi the Quest Type API
     */
    public IfElseEvent(final Argument<ConditionID> condition, final Argument<ActionID> event, final Argument<ActionID> elseEvent, final QuestTypeApi questTypeApi) {
        this.condition = condition;
        this.event = event;
        this.elseEvent = elseEvent;
        this.questTypeApi = questTypeApi;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        if (questTypeApi.condition(profile, condition.getValue(profile))) {
            questTypeApi.event(profile, event.getValue(profile));
        } else {
            questTypeApi.event(profile, elseEvent.getValue(profile));
        }
    }
}
