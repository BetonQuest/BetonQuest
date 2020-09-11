package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;

/**
 * Runs one or another event, depending of the condition outcome.
 */
public class IfElseEvent extends QuestEvent {

    private ConditionID condition;
    private EventID event;
    private EventID elseEvent;

    public IfElseEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        condition = instruction.getCondition();
        event = instruction.getEvent();
        if (!instruction.next().equalsIgnoreCase("else")) {
            throw new InstructionParseException("Missing 'else' keyword");
        }
        elseEvent = instruction.getEvent();
    }

    @Override
    protected Void execute(final String playerID) {
        if (BetonQuest.condition(playerID, condition)) {
            BetonQuest.event(playerID, event);
        } else {
            BetonQuest.event(playerID, elseEvent);
        }
        return null;
    }

}
