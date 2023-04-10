package org.betonquest.betonquest.api;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.logging.Level;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.MoreThanOneLogger"})
@ExtendWith(BetonQuestLoggerService.class)
class BetonQuestLoggerTest {
    /**
     * The {@link QuestPackage} name.
     */
    public static final String CUSTOM_CONFIG_PACKAGE = "CustomTestPackage";

    /**
     * The log message.
     */
    private static final String LOG_MESSAGE = "Test Message";

    /**
     * The processed topic of the logger from {@link BetonQuestLoggerService#LOGGER_TOPIC}.
     */
    private static final String LOGGER_TOPIC = "(" + BetonQuestLoggerService.LOGGER_TOPIC + ") ";

    /**
     * The log message with topic.
     */
    private static final String LOG_MESSAGE_WITH_TOPIC = LOGGER_TOPIC + LOG_MESSAGE;

    /**
     * The exception message.
     */
    private static final String EXCEPTION_MESSAGE = "Test Exception";

    private QuestPackage mockQuestPackage() {
        final QuestPackage questPackage = mock(QuestPackage.class);
        when(questPackage.getQuestPath()).thenReturn(CUSTOM_CONFIG_PACKAGE);
        return questPackage;
    }

    @Test
    void testDebug(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testDebugWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(mockQuestPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testDebugException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testDebugExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(mockQuestPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testInfo(final BetonQuestLogger log, final LogValidator logValidator) {
        log.info(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testInfoWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.info(mockQuestPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testWarn(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warn(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testWarnWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warn(mockQuestPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testWarnException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warn(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, LOGGER_TOPIC + "Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testWarnExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warn(mockQuestPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, LOGGER_TOPIC + "Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testError(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testErrorWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(mockQuestPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    void testErrorException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testErrorExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(mockQuestPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testReportException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.reportException(new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOGGER_TOPIC + "This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    void testReportExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.reportException(mockQuestPackage(), new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOGGER_TOPIC + "This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }
}
