package org.betonquest.betonquest.quest.objective.password;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;
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
        final String pattern = instruction.get(Argument.STRING).getValue(null);
        final int regexFlags = instruction.hasArgument("ignoreCase") ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        final Pattern regex = Pattern.compile(pattern, regexFlags);
        final String prefix = instruction.getValue("prefix");
        final String passwordPrefix = prefix == null || prefix.isEmpty() ? prefix : prefix + ": ";
        final Variable<List<EventID>> failEvents = instruction.getValueList("fail", EventID::new);
        return new PasswordObjective(instruction, regex, passwordPrefix, failEvents);
    }
}
