package org.betonquest.betonquest.utils.logger;

import org.betonquest.betonquest.config.ConfigPackage;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

@SuppressWarnings({"PMD.CommentRequired", "PMD.JUnitTestsShouldIncludeAssert"})
public class BetonQuestLoggerTest {
    private static final String LOGGER_TOPIC = "Test";
    private static final String LOG_MESSAGE = "Test Message";
    private static final String LOG_MESSAGE_WITH_TOPIC = "(" + LOGGER_TOPIC + ") Test Message";
    private static final String EXCEPTION_MESSAGE = "Test Exception";
    private static ConfigPackage PACKAGE;
    private static MockedStatic<BetonQuestLogger> mockedStaticBetonQuestLogger;
    private static BetonQuestLogger betonQuestLogger;
    private static LogValidator logValidator;

    public BetonQuestLoggerTest() {
    }

    @BeforeAll
    public static void beforeAll() {
        mockedStaticBetonQuestLogger = mockStatic(BetonQuestLogger.class);
        PACKAGE = mock(ConfigPackage.class);
        when(PACKAGE.getName()).thenReturn("CustomTestPackage");

        final JavaPlugin javaPlugin = mock(JavaPlugin.class);
        when(javaPlugin.getLogger()).thenReturn(Logger.getGlobal());
        betonQuestLogger = new BetonQuestLogger(javaPlugin, BetonQuestLoggerTest.class, LOGGER_TOPIC);

        final Logger logger = Logger.getGlobal();
        logValidator = new LogValidator();
        logger.addHandler(logValidator);
    }

    @AfterAll
    public static void afterAll() {
        mockedStaticBetonQuestLogger.close();
    }

    @AfterEach
    public void afterEach() {
        logValidator.assertEmpty();
    }

    @Test
    public void testDebug() {
        betonQuestLogger.debug(PACKAGE, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
    public void testDebugException() {
        betonQuestLogger.debug(PACKAGE, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.FINE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
    public void testInfo() {
        betonQuestLogger.info(PACKAGE, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
    public void testWarning() {
        betonQuestLogger.warning(PACKAGE, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
    public void testWarningException() {
        betonQuestLogger.warning(PACKAGE, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.WARNING, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertLogEntry(Level.FINE, "(Test) Additional stacktrace:", IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
    public void testError() {
        betonQuestLogger.error(PACKAGE, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC);
    }

    @Test
    public void testErrorException() {
        betonQuestLogger.error(PACKAGE, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
    }

    @Test
    public void testReportException() {
        betonQuestLogger.reportException(PACKAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, "(Test) This is an exception that should never occur. "
                        + "If you don't know why this occurs please report it to "
                        + "<https://github.com/BetonQuest/BetonQuest/issues>.",
                IOException.class, EXCEPTION_MESSAGE);
    }
}
