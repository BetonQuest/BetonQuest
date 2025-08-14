package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

/**
 * The if-else event. Either execute the one or the other event, depending on the condition.
 */
public class IfElseEvent implements NullableEvent {
    /**
     * The condition to check.
     */
    private final Variable<ConditionID> condition;

    /**
     * The event to run if the condition is true.
     */
    private final Variable<EventID> event;

    /**
     * The event to run if the condition is false.
     */
    private final Variable<EventID> elseEvent;

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
    public IfElseEvent(final Variable<ConditionID> condition, final Variable<EventID> event, final Variable<EventID> elseEvent, final QuestTypeApi questTypeApi) {
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
