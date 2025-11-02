package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.condition.ConditionID;
import org.betonquest.betonquest.api.quest.event.EventID;
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
        final Variable<ConditionID> condition = instruction.get(ConditionID::new);
        final Variable<EventID> event = instruction.get(EventID::new);
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final Variable<EventID> elseEvent = instruction.get(EventID::new);
        return new NullableEventAdapter(new IfElseEvent(condition, event, elseEvent, questTypeApi));
    }
}
