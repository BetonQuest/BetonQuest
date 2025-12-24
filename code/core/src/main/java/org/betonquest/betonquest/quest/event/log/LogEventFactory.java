package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to parse new {@link LogEvent}s.
 */
public class LogEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Regex used to detect a conditions statement at the end of the instruction.
     */
    private static final Pattern CONDITIONS_REGEX = Pattern.compile("conditions?:\\S*\\s*$");

    /**
     * Regex used to detect a level statement at the beginning of the instruction.
     */
    private static final Pattern LEVEL_REGEX = Pattern.compile("^\\s*level:\\S*\\s");

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new log event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public LogEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createLogEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createLogEvent(instruction);
    }

    private NullableEventAdapter createLogEvent(final Instruction instruction) throws QuestException {
        final Variable<LogEventLevel> level = instruction.enumeration(LogEventLevel.class).get("level", LogEventLevel.INFO);
        final String raw = String.join(" ", instruction.getValueParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(raw);
        final Matcher levelMatcher = LEVEL_REGEX.matcher(raw);
        final int msgStart = levelMatcher.find() ? levelMatcher.end() : 0;
        final int msgEnd = conditionsMatcher.find() ? conditionsMatcher.start() : raw.length();
        final Variable<String> message = instruction.get(raw.substring(msgStart, msgEnd), instruction.getParsers().string());
        return new NullableEventAdapter(new LogEvent(loggerFactory.create(LogEvent.class), level, message));
    }
}
