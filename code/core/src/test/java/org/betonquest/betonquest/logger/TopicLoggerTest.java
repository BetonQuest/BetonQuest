package org.betonquest.betonquest.logger;

import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * This class test the {@link TopicLogger}.
 */
class TopicLoggerTest {
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
     * The {@link Handler} for testing.
     */
    private Handler handler;

    /**
     * The {@link TopicLogger} to test.
     */
    private TopicLogger logger;

    @BeforeEach
    void setUp() {
        this.handler = mock(Handler.class);
        final Logger parentLogger = BetonQuestLoggerService.getSilentLogger();
        parentLogger.addHandler(handler);
        this.logger = new TopicLogger(parentLogger, TopicLoggerTest.class, LOGGER_TOPIC);
    }

    @Test
    void logLevelAndMessage() {
        logger.log(Level.INFO, LOG_MESSAGE);
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.INFO && LOG_MESSAGE_WITH_TOPIC.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }

    @Test
    void logLevelMessageExceptionAndExceptionMessage() {
        logger.log(Level.SEVERE, LOG_MESSAGE, new IOException(EXCEPTION_MESSAGE));
        verify(handler, times(1)).publish(argThat(record -> record.getLevel() == Level.SEVERE && LOG_MESSAGE_WITH_TOPIC.equals(record.getMessage())));
        verifyNoMoreInteractions(handler);
    }
}
