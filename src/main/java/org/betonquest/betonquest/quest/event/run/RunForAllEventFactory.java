package org.betonquest.betonquest.quest.event.run;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.CallStaticEventAdapter;
import org.betonquest.betonquest.utils.PlayerConverter;

import java.util.List;

/**
 * Create new {@link RunForAllEvent} from instruction.
 */
public class RunForAllEventFactory implements StaticEventFactory, EventFactory {
    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new CallStaticEventAdapter(parseStaticEvent(instruction));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws InstructionParseException {
        final List<EventID> eventIDS = instruction.getList(instruction.getOptional("events"), instruction::getEvent);
        eventIDS.addAll(instruction.getList(instruction.getOptional("event"), instruction::getEvent));
        final List<ConditionID> conditionIDS = instruction.getList(instruction.getOptional("where"), instruction::getCondition);
        return new RunForAllEvent(PlayerConverter::getOnlineProfiles, eventIDS, conditionIDS);
    }
}
