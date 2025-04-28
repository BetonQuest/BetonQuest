package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;

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
        final Variable<String> command = instruction.getVariable(Argument.STRING);
        final boolean ignoreCase = instruction.hasArgument("ignoreCase");
        final boolean exact = instruction.hasArgument("exact");
        final boolean cancel = instruction.hasArgument("cancel");
        final VariableList<EventID> failEvents = instruction.get(instruction.getOptional("failEvents", ""), PackageArgument.ofList(EventID::new));
        return new CommandObjective(instruction, command, ignoreCase, exact, cancel, failEvents);
    }
}
