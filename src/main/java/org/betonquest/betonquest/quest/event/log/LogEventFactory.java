package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to parse new {@link LogEvent}s.
 */
public class LogEventFactory implements EventFactory, StaticEventFactory {

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
     * Variable processor to create the message variable.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new log event factory.
     *
     * @param loggerFactory     BetonQuest logger factory used to retrieve the {@link BetonQuestLogger} for new events.
     * @param variableProcessor the variable processor for creating variables
     */
    public LogEventFactory(final BetonQuestLoggerFactory loggerFactory, final VariableProcessor variableProcessor) {
        this.loggerFactory = loggerFactory;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return createLogEvent(instruction);
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        return createLogEvent(instruction);
    }

    private NullableEventAdapter createLogEvent(final Instruction instruction) throws QuestException {
        final LogEventLevel level = instruction.getEnum(instruction.getOptional("level"), LogEventLevel.class, LogEventLevel.INFO);
        final String raw = String.join(" ", instruction.getAllParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(raw);
        final Matcher levelMatcher = LEVEL_REGEX.matcher(raw);
        final int msgStart = levelMatcher.find() ? levelMatcher.end() : 0;
        final int msgEnd = conditionsMatcher.find() ? conditionsMatcher.start() : raw.length();
        final VariableString message = new VariableString(variableProcessor, instruction.getPackage(), raw.substring(msgStart, msgEnd));
        return new NullableEventAdapter(new LogEvent(loggerFactory.create(LogEvent.class), level, message));
    }
}
