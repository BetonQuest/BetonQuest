package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.api.quest.event.HybridEventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to parse new {@link LogEvent}s.
 */
public class LogEventFactory implements HybridEventFactory {

    /**
     * Regex used to detect a conditions statement at the end of the instruction.
     */
    private static final Pattern CONDITIONS_REGEX = Pattern.compile("conditions?:\\S*\\s*$");

    /**
     * Regex used to detect a level statement at the beginning of the instruction.
     */
    private static final Pattern LEVEL_REGEX = Pattern.compile("^\\s*level:\\S*\\s");

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new log event factory.
     *
     * @param loggerFactory BetonQuest logger factory used to retrieve the {@link BetonQuestLogger} for new events.
     */
    public LogEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public HybridEvent parseHybridEvent(final Instruction instruction) throws InstructionParseException {
        final LogEventLevel level = instruction.getEnum(instruction.getOptional("level"), LogEventLevel.class, LogEventLevel.INFO);
        final String raw = String.join(" ", instruction.getAllParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(raw);
        final Matcher levelMatcher = LEVEL_REGEX.matcher(raw);
        final int msgStart = levelMatcher.find() ? levelMatcher.end() : 0;
        final int msgEnd = conditionsMatcher.find() ? conditionsMatcher.start() : raw.length();
        final VariableString message = new VariableString(instruction.getPackage(), raw.substring(msgStart, msgEnd));
        return new LogEvent(loggerFactory.create(LogEvent.class), level, message);
    }
}
