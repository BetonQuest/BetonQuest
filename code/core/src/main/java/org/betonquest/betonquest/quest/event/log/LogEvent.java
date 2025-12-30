package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Prints a simple message to the server log.
 */
public class LogEvent implements NullableEvent {

    /**
     * The logger used for logging messages.
     */
    private final BetonQuestLogger logger;

    /**
     * The level to log the message at.
     */
    private final Argument<LogEventLevel> level;

    /**
     * The message that should be printed to the server log.
     */
    private final Argument<String> message;

    /**
     * Create a new {@link LogEvent}.
     *
     * @param logger  the logger used for logging messages
     * @param level   the level to log the message at
     * @param message the message that should be printed to the server log
     */
    public LogEvent(final BetonQuestLogger logger, final Argument<LogEventLevel> level, final Argument<String> message) {
        this.logger = logger;
        this.level = level;
        this.message = message;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        level.getValue(profile).log(logger, message.getValue(profile));
    }
}
