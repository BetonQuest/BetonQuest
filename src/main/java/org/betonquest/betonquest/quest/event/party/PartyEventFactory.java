package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;

/**
 * Fires specified events for every player in the party
 */
public class PartyEventFactory implements EventFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a PartyEventFactory instance
     */
    public PartyEventFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final VariableNumber range = instruction.getVarNum();
        final VariableNumber amount = instruction.getVarNum(instruction.getOptional("amount"));
        final ConditionID[] conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        final EventID[] events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        return new OnlineProfileRequiredEvent(log, new PartyEvent(range, amount, conditions, events), instruction.getPackage());
    }
}
