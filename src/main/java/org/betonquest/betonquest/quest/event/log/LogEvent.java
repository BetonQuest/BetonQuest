package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.VariableString;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Prints a simple message to the server log.
 */
public class LogEvent implements Event {

    /**
     * Message to log.
     */
    private final VariableString message;

    /**
     * Logger to use.
     */
    private final BetonQuestLogger logger;

    /**
     * Create a new {@link LogEvent}
     *
     * @param logger logger used for logging messages.
     * @param message message that should be printed to the server log.
     */
    public LogEvent(final BetonQuestLogger logger, final VariableString message) {
        this.logger = logger;
        this.message = message;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        logger.info(message.getString(profile));
    }
}
