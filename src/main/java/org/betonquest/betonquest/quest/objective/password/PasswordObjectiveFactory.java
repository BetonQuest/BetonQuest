package org.betonquest.betonquest.quest.objective.password;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.Instruction;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Factory for creating {@link PasswordObjective} instances from {@link Instruction}s.
 */
public class PasswordObjectiveFactory implements ObjectiveFactory {

    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the PasswordObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public PasswordObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final String pattern = instruction.next();
        final int regexFlags = instruction.hasArgument("ignoreCase") ? Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE : 0;
        final Pattern regex = Pattern.compile(pattern, regexFlags);
        final String prefix = instruction.getOptional("prefix");
        final String passwordPrefix = prefix == null || prefix.isEmpty() ? prefix : prefix + ": ";
        final List<EventID> failEvents = instruction.getIDList(instruction.getOptional("fail"), EventID::new);
        final BetonQuestLogger log = loggerFactory.create(PasswordObjective.class);
        return new PasswordObjective(instruction, log, regex, passwordPrefix, failEvents);
    }
}
