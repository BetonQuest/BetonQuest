package org.betonquest.betonquest.quest.event.party;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
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
     * Creates a PartyEventFactory instance
     */
    public PartyEventFactory() {
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final VariableNumber range = instruction.getVarNum();
        final ConditionID[] conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        final EventID[] events = instruction.getList(instruction::getEvent).toArray(new EventID[0]);
        final String amountString = instruction.getOptional("amount");
        final VariableNumber amount = amountString != null ? instruction.getVarNum(amountString) : null;
        return new OnlineProfileRequiredEvent(new PartyEvent(range, amount, conditions, events), instruction.getPackage());
    }
}
