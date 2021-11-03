package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerValidationProvider;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.util.logging.Level;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.MoreThanOneLogger"})
@ExtendWith(BetonQuestLoggerValidationProvider.class)
@Execution(ExecutionMode.SAME_THREAD)
public class BetonQuestLoggerTest {
    /**
     * The {@link ConfigPackage} name.
     */
    public static final String CUSTOM_CONFIG_PACKAGE = "CustomTestPackage";
    /**
     * The log message.
     */
    private static final String LOG_MESSAGE = "Test Message";
    /**
     * The processed topic of the logger from {@link BetonQuestLoggerValidationProvider#LOGGER_TOPIC}.
     */
    private static final String LOGGER_TOPIC = "(" + BetonQuestLoggerValidationProvider.LOGGER_TOPIC + ") ";
    /**
     * The log message with topic.
     */
    private static final String LOG_MESSAGE_WITH_TOPIC = LOGGER_TOPIC + LOG_MESSAGE;
    /**
     * The exception message.
     */
    private static final String EXCEPTION_MESSAGE = "Test Exception";

    /**
     * Default constructor.
     */
    public BetonQuestLoggerTest() {
    }

    private ConfigPackage mockConfigPackage() {
        final ConfigPackage configPackage = mock(ConfigPackage.class);
        when(configPackage.getName()).thenReturn(CUSTOM_CONFIG_PACKAGE);
        return configPackage;
    }

    @Test
    public void testDebug(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testDebugWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testDebugException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testDebugExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.debug(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testInfo(final BetonQuestLogger log, final LogValidator logValidator) {
        log.info(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testInfoWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.info(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarning(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warning(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarningWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warning(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarningException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warning(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, LOGGER_TOPIC + "Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarningExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.warning(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, LOGGER_TOPIC + "Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testError(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testErrorWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testErrorException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testErrorExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.error(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testReportException(final BetonQuestLogger log, final LogValidator logValidator) {
        log.reportException(new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOGGER_TOPIC + "This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testReportExceptionWithPackage(final BetonQuestLogger log, final LogValidator logValidator) {
        log.reportException(mockConfigPackage(), new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOGGER_TOPIC + "This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }
}
