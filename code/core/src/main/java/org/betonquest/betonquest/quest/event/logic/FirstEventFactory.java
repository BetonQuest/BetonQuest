package org.betonquest.betonquest.quest.event.logic;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;

import java.util.List;

/**
 * Factory to create FirstEvents from events from {@link Instruction}s.
 */
public class FirstEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Empty constructor.
     *
     * @param questTypeApi the Quest Type API
     */
    public FirstEventFactory(final QuestTypeApi questTypeApi) {
        this.questTypeApi = questTypeApi;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createFirstEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createFirstEvent(instruction);
    }

    private NullableEventAdapter createFirstEvent(final Instruction instruction) throws QuestException {
        final Variable<List<EventID>> list = instruction.parse(EventID::new).getList();
        return new NullableEventAdapter(new FirstEvent(list, questTypeApi));
    }
}
