package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.nullable.NullableAction;
import org.jetbrains.annotations.Nullable;

/**
 * Prints a simple message to the server log.
 */
public class LogEvent implements NullableAction {

    /**
     * Message to log.
     */
    private final Argument<String> message;

    /**
     * Logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * Level to log the message at.
     */
    private final Argument<LogEventLevel> level;

    /**
     * Create a new {@link LogEvent}.
     *
     * @param logger  logger used for logging messages.
     * @param level   level to log the message at.
     * @param message message that should be printed to the server log.
     */
    public LogEvent(final BetonQuestLogger logger, final Argument<LogEventLevel> level, final Argument<String> message) {
        this.logger = logger;
        this.message = message;
        this.level = level;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        level.getValue(profile).log(logger, message.getValue(profile));
    }
}
