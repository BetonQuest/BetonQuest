package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

import java.util.List;

/**
 * Fires specified events for every player in the party.
 */
public class PartyEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a PartyEventFactory instance.
     *
     * @param loggerFactory logger factory to use
     */
    public PartyEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber range = instruction.get(VariableNumber::new);
        final VariableNumber amount = instruction.get(instruction.getOptional("amount"), VariableNumber::new);
        final List<ConditionID> conditions = instruction.getIDList(ConditionID::new);
        final List<EventID> events = instruction.getIDList(EventID::new);
        return new OnlineEventAdapter(
                new PartyEvent(range, amount, conditions, events),
                loggerFactory.create(PartyEvent.class),
                instruction.getPackage()
        );
    }
}
