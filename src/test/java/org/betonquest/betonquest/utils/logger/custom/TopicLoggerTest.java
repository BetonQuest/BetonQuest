package org.betonquest.betonquest.utils.logger.custom;

import org.betonquest.betonquest.utils.logger.LogValidator;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"PMD.CommentRequired", "PMD.JUnitTestsShouldIncludeAssert"})
public class TopicLoggerTest {
    private static TopicLogger logger;

    public TopicLoggerTest() {
    }

    @BeforeAll
    public static void beforeAll() {
        final JavaPlugin javaPlugin = mock(JavaPlugin.class);
        when(javaPlugin.getLogger()).thenReturn(Logger.getGlobal());
        logger = new TopicLogger(javaPlugin, TopicLoggerTest.class, "Test");
    }

    @Test
    public void testLogLevelAndMessage() {
        final LogValidator logValidator = new LogValidator();
        logger.addHandler(logValidator);

        logger.log(Level.INFO, "Test Message");

        logValidator.assertLogEntry(Level.INFO, "(Test) Test Message");
        logValidator.assertEmpty();
    }

    @Test
    public void testLogLevelMessageAndException() {
        final LogValidator logValidator = new LogValidator();
        logger.addHandler(logValidator);

        logger.log(Level.SEVERE, "Test Message", new IOException("Test Exception!"));

        logValidator.assertLogEntry(Level.SEVERE, "(Test) Test Message", IOException.class);
        logValidator.assertEmpty();
    }

    @Test
    public void testLogLevelMessageExceptionAndExceptionMessage() {
        final LogValidator logValidator = new LogValidator();
        logger.addHandler(logValidator);

        logger.log(Level.SEVERE, "Test Message", new IOException("Test Exception!"));

        logValidator.assertLogEntry(Level.SEVERE, "(Test) Test Message", IOException.class, "Test Exception!");
        logValidator.assertEmpty();
    }
}
