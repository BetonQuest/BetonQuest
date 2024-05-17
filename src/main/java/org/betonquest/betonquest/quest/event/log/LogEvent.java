package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.HybridEvent;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.jetbrains.annotations.Nullable;

/**
 * Prints a simple message to the server log.
 */
public class LogEvent implements HybridEvent {

    /**
     * Message to log.
     */
    private final VariableString message;

    /**
     * Logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * Level to log the message at.
     */
    private final LogEventLevel level;

    /**
     * Create a new {@link LogEvent}.
     *
     * @param logger  logger used for logging messages.
     * @param level   level to log the message at.
     * @param message message that should be printed to the server log.
     */
    public LogEvent(final BetonQuestLogger logger, final LogEventLevel level, final VariableString message) {
        this.logger = logger;
        this.message = message;
        this.level = level;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestRuntimeException {
        level.log(logger, message.getString(profile));
    }
}
