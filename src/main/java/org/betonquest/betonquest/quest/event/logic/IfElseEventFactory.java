package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * Factory to create if-else events from {@link Instruction}s.
 */
public class IfElseEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The event constructor.
     *
     * @param questTypeAPI the Quest Type API
     */
    public IfElseEventFactory(final QuestTypeAPI questTypeAPI) {
        this.questTypeAPI = questTypeAPI;
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
        final ConditionID condition = instruction.getID(ConditionID::new);
        final EventID event = instruction.getID(EventID::new);
        if (!"else".equalsIgnoreCase(instruction.next())) {
            throw new QuestException("Missing 'else' keyword");
        }
        final EventID elseEvent = instruction.getID(EventID::new);
        return new NullableEventAdapter(new IfElseEvent(condition, event, elseEvent, questTypeAPI));
    }
}
