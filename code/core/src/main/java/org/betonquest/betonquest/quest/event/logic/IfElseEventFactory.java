package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;

/**
 * Factory to create if-else events from {@link Instruction}s.
 */
public class IfElseEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * The keyword to identify an else event following.
     */
    private static final String ELSE_KEYWORD = "else";

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * The event constructor.
     *
     * @param questTypeApi the Quest Type API
     */
    public IfElseEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createIfElseEvent(instruction);
    }

    private NullableEventAdapter createIfElseEvent(final Instruction instruction) throws QuestException {
        final Argument<ConditionID> condition = instruction.parse(ConditionID::new).get();
        final Argument<ActionID> event = instruction.parse(ActionID::new).get();
        if (!ELSE_KEYWORD.equalsIgnoreCase(instruction.nextElement())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final Argument<ActionID> elseEvent = instruction.parse(ActionID::new).get();
        return new NullableEventAdapter(new IfElseEvent(condition, event, elseEvent, questTypeApi));
    }
}
