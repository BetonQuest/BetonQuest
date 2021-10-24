package org.betonquest.betonquest.api.logger;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.util.LogValidator;
import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.utils.logger.BetonQuestLoggerImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.MoreThanOneLogger"})
@Execution(ExecutionMode.CONCURRENT)
public class BetonQuestLoggerTest {
    /**
     * The {@link ConfigPackage} name.
     */
    public static final String CUSTOM_CONFIG_PACKAGE = "CustomTestPackage";
    /**
     * The logger topic.
     */
    private static final String LOGGER_TOPIC = "Test";
    /**
     * The log message.
     */
    private static final String LOG_MESSAGE = "Test Message";
    /**
     * The log message with topic.
     */
    private static final String LOG_MESSAGE_WITH_TOPIC = "(" + LOGGER_TOPIC + ") " + LOG_MESSAGE;
    /**
     * The exception message.
     */
    private static final String EXCEPTION_MESSAGE = "Test Exception";
    /**
     * The static mocked {@link BetonQuestLogger} instance.
     * This is needed, because the {@link ConfigPackage} uses a @{@link lombok.CustomLog} annotation,
     * and we want to mock the {@link ConfigPackage}.
     */
    private static MockedStatic<BetonQuestLogger> betonQuestLoggerMockedStatic;

    /**
     * Default constructor.
     */
    public BetonQuestLoggerTest() {
    }

    /**
     * Set up the static mocked {@link BetonQuestLogger}.
     */
    @BeforeAll
    public static void beforeAll() {
        betonQuestLoggerMockedStatic = mockStatic(BetonQuestLogger.class);
    }

    /**
     * Close the static mocked {@link BetonQuestLogger}.
     */
    @AfterAll
    public static void afterAll() {
        betonQuestLoggerMockedStatic.close();
    }

    private ConfigPackage mockConfigPackage() {
        final ConfigPackage configPackage = mock(ConfigPackage.class);
        when(configPackage.getName()).thenReturn(CUSTOM_CONFIG_PACKAGE);
        return configPackage;
    }

    private BetonQuestLogger getBetonQuestLogger(final Logger logger) {
        return new BetonQuestLoggerImpl(null, logger, BetonQuestLoggerTest.class, LOGGER_TOPIC);
    }

    @Test
    public void testDebug() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.debug(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testDebugException() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.debug(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testInfo() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.info(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarning() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.warning(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testWarningException() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.warning(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, "(Test) Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testError() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.error(mockConfigPackage(), LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    public void testErrorException() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.error(mockConfigPackage(), LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }

    @Test
    public void testReportException() {
        final Logger logger = LogValidator.getSilentLogger();
        final BetonQuestLogger betonQuestLogger = getBetonQuestLogger(logger);
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        betonQuestLogger.reportException(mockConfigPackage(), new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, "(Test) This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }
}
