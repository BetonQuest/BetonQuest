package org.betonquest.betonquest.quest.objective.password;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.VariableList;

import java.util.regex.Pattern;

/**
 * Factory for creating {@link PasswordObjective} instances from {@link Instruction}s.
 */
public class PasswordObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the PasswordObjectiveFactory.
     */
    public PasswordObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final String pattern = instruction.next();
        final int regexFlags = instruction.hasArgument("ignoreCase") ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        final Pattern regex = Pattern.compile(pattern, regexFlags);
        final String prefix = instruction.getOptional("prefix");
        final String passwordPrefix = prefix == null || prefix.isEmpty() ? prefix : prefix + ": ";
        final VariableList<EventID> failEvents = instruction.get(instruction.getOptional("fail", ""), IDArgument.ofList(EventID::new));
        return new PasswordObjective(instruction, regex, passwordPrefix, failEvents);
    }
}
