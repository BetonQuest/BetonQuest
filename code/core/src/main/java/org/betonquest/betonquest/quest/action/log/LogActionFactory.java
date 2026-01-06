package org.betonquest.betonquest.quest.action.log;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to parse new {@link LogAction}s.
 */
public class LogActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Regex used to detect a conditions statement at the end of the instruction.
     */
    private static final Pattern CONDITIONS_REGEX = Pattern.compile("conditions?:\\S*\\s*$");

    /**
     * Regex used to detect a level statement at the beginning of the instruction.
     */
    private static final Pattern LEVEL_REGEX = Pattern.compile("^\\s*level:\\S*\\s");

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new log action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public LogActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createLogAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createLogAction(instruction);
    }

    private NullableActionAdapter createLogAction(final Instruction instruction) throws QuestException {
        final Argument<LogEventLevel> level = instruction.enumeration(LogEventLevel.class).get("level", LogEventLevel.INFO);
        final String raw = String.join(" ", instruction.getValueParts());
        final Matcher conditionsMatcher = CONDITIONS_REGEX.matcher(raw);
        final Matcher levelMatcher = LEVEL_REGEX.matcher(raw);
        final int msgStart = levelMatcher.find() ? levelMatcher.end() : 0;
        final int msgEnd = conditionsMatcher.find() ? conditionsMatcher.start() : raw.length();
        final Argument<String> message = instruction.chainForArgument(raw.substring(msgStart, msgEnd)).string().get();
        return new NullableActionAdapter(new LogAction(loggerFactory.create(LogAction.class), level, message));
    }
}
