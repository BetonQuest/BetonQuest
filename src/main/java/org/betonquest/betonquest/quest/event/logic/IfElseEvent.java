package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.jetbrains.annotations.Nullable;

/**
 * The if-else event. Either execute the one or the other event, depending on the condition.
 */
public class IfElseEvent implements NullableEvent {
    /**
     * The condition to check.
     */
    private final ConditionID condition;

    /**
     * The event to run if the condition is true.
     */
    private final EventID event;

    /**
     * The event to run if the condition is false.
     */
    private final EventID elseEvent;

    /**
     * Creates a new if-else event.
     *
     * @param condition the condition to check
     * @param event     the event to run if the condition is true
     * @param elseEvent the event to run if the condition is false
     */
    public IfElseEvent(final ConditionID condition, final EventID event, final EventID elseEvent) {
        this.condition = condition;
        this.event = event;
        this.elseEvent = elseEvent;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        if (BetonQuest.condition(profile, condition)) {
            BetonQuest.event(profile, event);
        } else {
            BetonQuest.event(profile, elseEvent);
        }
    }
}
