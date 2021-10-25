package org.betonquest.betonquest.modules.logger;

import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class test the {@link TopicLogger}.
 */
@SuppressWarnings("PMD.MoreThanOneLogger")
@Execution(ExecutionMode.CONCURRENT)
public class TopicLoggerTest {
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
     * Default constructor.
     */
    public TopicLoggerTest() {
    }

    private TopicLogger getTopicLogger() {
        return new TopicLogger(LogValidator.getSilentLogger(), TopicLoggerTest.class, LOGGER_TOPIC);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testLogLevelAndMessage() {
        final Logger logger = getTopicLogger();
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        logger.log(Level.INFO, LOG_MESSAGE);
        logValidator.assertLogEntry(Level.INFO, LOG_MESSAGE_WITH_TOPIC);
        logValidator.assertEmpty();
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testLogLevelMessageExceptionAndExceptionMessage() {
        final Logger logger = getTopicLogger();
        final LogValidator logValidator = LogValidator.getForLogger(logger);
        logger.log(Level.SEVERE, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        logValidator.assertLogEntry(Level.SEVERE, LOG_MESSAGE_WITH_TOPIC, IOException.class, EXCEPTION_MESSAGE);
        logValidator.assertEmpty();
    }
}
