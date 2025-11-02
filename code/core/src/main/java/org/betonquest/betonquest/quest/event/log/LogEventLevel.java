package org.betonquest.betonquest.quest.event.log;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.function.BiConsumer;

/**
 * Human-readable log level used by the {@link LogEvent}.
 * <p>
 * While there is already a ton of similar enums for log levels,
 * they are all not really suitable for this use case for multiple reasons.
 * Some of them use different names for the levels, some have different levels (e.g. TRACE, ALL),
 * some are not direct dependencies of BetonQuest and most of them are not compatible with the {@link BetonQuestLogger}.
 */
public enum LogEventLevel {
    /**
     * Level used for normal messages, default level.
     */
    INFO(BetonQuestLogger::info),

    /**
     * Level used for debug messages.
     */
    DEBUG(BetonQuestLogger::debug),

    /**
     * Level used for warnings.
     */
    WARNING(BetonQuestLogger::warn),

    /**
     * Level used for errors.
     */
    ERROR(BetonQuestLogger::error);

    /**
     * Method of the {@link BetonQuestLogger} used to log the message.
     */
    private final BiConsumer<BetonQuestLogger, String> logFunction;

    /**
     * Create a new {@link LogEventLevel}.
     *
     * @param logFunction method of the {@link BetonQuestLogger} used to log the message.
     */
    LogEventLevel(final BiConsumer<BetonQuestLogger, String> logFunction) {
        this.logFunction = logFunction;
    }

    /**
     * Log the message with the given logger at this level.
     *
     * @param logger  logger used to log the message.
     * @param message message to log.
     */
    public void log(final BetonQuestLogger logger, final String message) {
        logFunction.accept(logger, message);
    }
}
