package org.betonquest.betonquest.api.logger;

import org.betonquest.betonquest.api.logger.util.LogValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class test the {@link TopicLogger}.
 */
@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
public class TopicLoggerTest {
    /**
     * The {@link TopicLogger} instance to test.
     */
    private TopicLogger logger;
    /**
     * The {@link LogValidator} instance.
     */
    private LogValidator logValidator;

    /**
     * Default constructor.
     */
    public TopicLoggerTest() {
    }

    /**
     * Setup the {@link LogValidator}.
     */
    @BeforeEach
    public void beforeEach() {
        logger = new TopicLogger(Logger.getGlobal(), TopicLoggerTest.class, "TestTopicLogger");
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
        /* default */ void testLogLevelAndMessage() {
        logger.log(Level.INFO, "Test Message");
        logValidator.assertLogEntry(Level.INFO, "(TestTopicLogger) Test Message");
    }

    @Test
        /* default */ void testLogLevelMessageAndException() {
        logger.log(Level.SEVERE, "Test Message", new IOException("Test Exception!"));
        logValidator.assertLogEntry(Level.SEVERE, "(TestTopicLogger) Test Message", IOException.class);
    }

    @Test
        /* default */ void testLogLevelMessageExceptionAndExceptionMessage() {
        logger.log(Level.SEVERE, "Test Message", new IOException("Test Exception!"));
        logValidator.assertLogEntry(Level.SEVERE, "(TestTopicLogger) Test Message", IOException.class, "Test Exception!");
    }
}
