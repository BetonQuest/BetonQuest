package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.VariableArgument;
import org.betonquest.betonquest.instruction.variable.VariableString;

import java.util.List;

/**
 * Factory for creating {@link CommandObjective} instances from {@link Instruction}s.
 */
public class CommandObjectiveFactory implements ObjectiveFactory {
    /**
     * Creates a new instance of the CommandObjectiveFactory.
     */
    public CommandObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final VariableString command = instruction.get(VariableArgument.STRING_REPLACE_UNDERSCORES);
        final boolean ignoreCase = instruction.hasArgument("ignoreCase");
        final boolean exact = instruction.hasArgument("exact");
        final boolean cancel = instruction.hasArgument("cancel");
        final List<EventID> failEvents = instruction.getIDList(instruction.getOptional("failEvents"), EventID::new);
        return new CommandObjective(instruction, command, ignoreCase, exact, cancel, failEvents);
    }
}
