package org.betonquest.betonquest.api.logger;

import org.betonquest.betonquest.api.logger.util.LogValidator;
import org.betonquest.betonquest.config.ConfigPackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link BetonQuestLogger}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.TooManyMethods"})
public class BetonQuestLoggerTest {
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
    private static final String LOG_MESSAGE_WITH_TOPIC = "(" + LOGGER_TOPIC + ") Test Message";
    /**
     * The exception message.
     */
    private static final String EXCEPTION_MESSAGE = "Test Exception";
    /**
     * The static mocked {@link BetonQuestLogger} instance.
     */
    private static MockedStatic<BetonQuestLogger> betonQuestLoggerMockedStatic;
    /**
     * The {@link ConfigPackage} instance.
     */
    private ConfigPackage configPackage;
    /**
     * The {@link BetonQuestLogger} instance.
     */
    private BetonQuestLogger betonQuestLogger;
    /**
     * The {@link LogValidator} instance.
     */
    private LogValidator logValidator;

    /**
     * Default constructor.
     */
    public BetonQuestLoggerTest() {
    }

    /**
     * Setup the static mocked {@link BetonQuestLogger}.
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

    /**
     * Setup the {@link BetonQuestLogger} and {@link LogValidator}.
     */
    @BeforeEach
    public void beforeEach() {
        configPackage = mock(ConfigPackage.class);
        when(configPackage.getName()).thenReturn("CustomTestPackage");

        betonQuestLogger = new BetonQuestLogger(Logger.getGlobal(), BetonQuestLoggerTest.class, LOGGER_TOPIC);

        final Logger logger = Logger.getGlobal();
        logValidator = new LogValidator();
        logger.addHandler(logValidator);
    }

    /**
     * Assert that the {@link LogValidator} is empty.
     */
    @AfterEach
    public void afterEach() {
        logValidator.assertEmpty();
    }

    @Test
        /* default */ void testDebug() {
        betonQuestLogger.debug(configPackage, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
        /* default */ void testDebugException() {
        betonQuestLogger.debug(configPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
        /* default */ void testInfo() {
        betonQuestLogger.info(configPackage, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
        /* default */ void testWarning() {
        betonQuestLogger.warning(configPackage, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
        /* default */ void testWarningException() {
        betonQuestLogger.warning(configPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, "(Test) Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
        /* default */ void testError() {
        betonQuestLogger.error(configPackage, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
        /* default */ void testErrorException() {
        betonQuestLogger.error(configPackage, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
        /* default */ void testReportException() {
        betonQuestLogger.reportException(configPackage, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, "(Test) This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to the author.",
                IOException.class, EXCEPTION_MESSAGE);
    }
}
