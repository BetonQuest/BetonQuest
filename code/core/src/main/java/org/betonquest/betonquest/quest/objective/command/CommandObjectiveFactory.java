package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.Collections;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> command = instruction.string().get();
        final FlagArgument<Boolean> ignoreCase = instruction.bool().getFlag("ignoreCase", true);
        final FlagArgument<Boolean> exact = instruction.bool().getFlag("exact", true);
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<List<ActionID>> failEvents = instruction.parse(ActionID::new)
                .list().get("failActions", Collections.emptyList());
        return new CommandObjective(instruction, command, ignoreCase, exact, cancel, failEvents);
    }
}
