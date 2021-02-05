package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;

/**
 * Runs one or another event, depending of the condition outcome.
 */
@SuppressWarnings("PMD.CommentRequired")
public class IfElseEvent extends QuestEvent {

    private final ConditionID condition;
    private final EventID event;
    private final EventID elseEvent;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public IfElseEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        condition = instruction.getCondition();
        event = instruction.getEvent();
        if (!"else".equalsIgnoreCase(instruction.next())) {
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
