package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;

/**
 * Fires specified events for every player in the party
 */
public class PartyEvent extends QuestEvent {

    private ConditionID[] conditions;
    private EventID[] events;
    private VariableNumber range;

    public PartyEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        range = instruction.getVarNum();
        conditions = instruction.getList(e -> instruction.getCondition(e)).toArray(new ConditionID[0]);
        events = instruction.getList(e -> instruction.getEvent(e)).toArray(new EventID[0]);
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final ArrayList<String> members = Utils.getParty(playerID, range.getDouble(playerID), instruction.getPackage()
                .getName(), conditions);
        for (final String memberID : members) {
            for (final EventID event : events) {
                BetonQuest.event(memberID, event);
            }
        }
        return null;
    }

}
