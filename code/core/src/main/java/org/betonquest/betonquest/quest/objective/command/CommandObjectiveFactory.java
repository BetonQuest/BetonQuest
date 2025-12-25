package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.EventID;
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
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> command = instruction.string().get();
        final boolean ignoreCase = instruction.hasArgument("ignoreCase");
        final boolean exact = instruction.hasArgument("exact");
        final boolean cancel = instruction.hasArgument("cancel");
        final Argument<List<EventID>> failEvents = instruction.parse(EventID::new)
                .getList("failEvents", Collections.emptyList());
        return new CommandObjective(instruction, command, ignoreCase, exact, cancel, failEvents);
    }
}
