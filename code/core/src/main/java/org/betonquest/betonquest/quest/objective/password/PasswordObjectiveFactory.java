package org.betonquest.betonquest.quest.objective.password;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.Collections;
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
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final String pattern = instruction.string().get().getValue(null);
        final FlagArgument<Pattern> regex = instruction.bool()
                .map(ignCase -> ignCase
                        ? Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE) : Pattern.compile(pattern))
                .getFlag("ignoreCase", Pattern.compile(pattern));
        final Argument<String> prefix = instruction.string().get("prefix").orElse(null);
        final String resolvedPrefix = prefix == null ? null : prefix.getValue(null);
        final String passwordPrefix = resolvedPrefix == null || resolvedPrefix.isEmpty() ? resolvedPrefix : resolvedPrefix + ": ";
        final Argument<List<EventID>> failEvents = instruction.parse(EventID::new).list().get("fail", Collections.emptyList());
        return new PasswordObjective(instruction, regex, passwordPrefix, failEvents);
    }
}
