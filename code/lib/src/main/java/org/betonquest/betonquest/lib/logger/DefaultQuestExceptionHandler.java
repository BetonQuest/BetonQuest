package org.betonquest.betonquest.lib.logger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.LogSource;

/**
 * Can handle thrown {@link QuestException} and rate limits them so
 * they don't spam console that hard.
 */
public class DefaultQuestExceptionHandler {

    /**
     * The default minimal interval in which errors are logged.
     */
    public static final int DEFAULT_ERROR_RATE_LIMIT_MILLIS = 5000;

    /**
     * The minimal interval in which errors are logged.
     */
    private final int errorRateLimit;

    /**
     * The logger instance to use.
     */
    private final BetonQuestLogger logger;

    /**
     * The associated source for logging.
     */
    private final LogSource source;

    /**
     * All additional source details to log.
     */
    private final String sourceDetails;

    /**
     * The last {@link System#currentTimeMillis()} timestamp when an error message was logged.
     */
    private long last;

    /**
     * Creates a new {@link DefaultQuestExceptionHandler} instance.
     *
     * @param source         the source to use for logging
     * @param logger         the logger to use
     * @param errorRateLimit the minimal interval in which errors are logged
     * @param sourceDetails  additional source details to log
     */
    public DefaultQuestExceptionHandler(final LogSource source, final BetonQuestLogger logger,
                                        final int errorRateLimit, final String... sourceDetails) {
        this.logger = logger;
        this.errorRateLimit = errorRateLimit;
        this.source = source;
        this.sourceDetails = sourceDetails.length > 0 ? "{'" + String.join("', '", sourceDetails) + "'}" : "";
    }

    /**
     * Creates a new {@link DefaultQuestExceptionHandler} instance with an error rate limit of DEFAULT_ERROR_RATE_LIMIT_MILLIS.
     *
     * @param source        the source to use for logging
     * @param logger        the logger to use
     * @param sourceDetails additional source details to log
     */
    public DefaultQuestExceptionHandler(final LogSource source, final BetonQuestLogger logger, final String... sourceDetails) {
        this(source, logger, DEFAULT_ERROR_RATE_LIMIT_MILLIS, sourceDetails);
    }

    /**
     * Runs a task and logs occurring quest exceptions with a rate limit.
     *
     * @param qeThrowing   a task that may throw a quest exception
     * @param defaultValue the default value to return in case of an exception
     * @param <T>          the type of the result
     * @return the result of the task or the default value if an exception occurs
     */
    public <T> T handle(final QuestSupplier<T> qeThrowing, final T defaultValue) {
        try {
            return qeThrowing.get();
        } catch (final QuestException e) {
            if (System.currentTimeMillis() - last >= errorRateLimit) {
                last = System.currentTimeMillis();
                logger.warn(source, "%sError while handling: ".formatted(sourceDetails) + e.getMessage(), e);
            }
            return defaultValue;
        }
    }

    /**
     * Runs a task and logs occurring quest exceptions with a rate limit.
     *
     * @param qeThrowing a task that may throw a quest exception
     */
    @SuppressWarnings("NullAway")
    public void handle(final QuestRunnable qeThrowing) {
        handle(() -> {
            qeThrowing.run();
            return null;
        }, null);
    }
}
