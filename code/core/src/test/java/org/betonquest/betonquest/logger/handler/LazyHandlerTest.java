package org.betonquest.betonquest.logger.handler;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for a {@link LazyHandler}.
 */
@ExtendWith({MockitoExtension.class, BetonQuestLoggerService.class})
class LazyHandlerTest {
    /**
     * Example record to use with tests.
     */
    private final LogRecord record = new LogRecord(Level.INFO, "test message");

    /**
     * Handler for the supplier to test with.
     */
    @Mock
    private Handler internalHandler;

    @Test
    void testLazyInstantiation(final BetonQuestLogger log, @Mock final LazyHandler.LazyHandlerSupplier handlerSupplier) throws IOException {
        when(handlerSupplier.get()).thenReturn(internalHandler);
        final LazyHandler handler = new LazyHandler(log, handlerSupplier);
        verify(handlerSupplier, never()).get();
        handler.publish(record);
        verify(handlerSupplier).get();
    }

    @Test
    void testFlushDoesNotCauseInitialization(final BetonQuestLogger log, @Mock final LazyHandler.LazyHandlerSupplier handlerSupplier) throws IOException {
        final LazyHandler handler = new LazyHandler(log, handlerSupplier);
        handler.flush();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testCloseDoesNotCauseInitialization(final BetonQuestLogger log, @Mock final LazyHandler.LazyHandlerSupplier handlerSupplier) throws IOException {
        final LazyHandler handler = new LazyHandler(log, handlerSupplier);
        handler.close();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testPublishAfterClosingDoesNotCauseInitialization(final BetonQuestLogger log, @Mock final LazyHandler.LazyHandlerSupplier handlerSupplier) throws IOException {
        final LazyHandler handler = new LazyHandler(log, handlerSupplier);
        handler.close();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testPublishIsPropagated(final BetonQuestLogger log) {
        final LazyHandler handler = new LazyHandler(log, () -> internalHandler);
        handler.publish(record);
        verify(internalHandler).publish(record);
    }

    @Test
    void testFlushIsPropagated(final BetonQuestLogger log) {
        final LazyHandler handler = new LazyHandler(log, () -> internalHandler);
        handler.publish(record);
        handler.flush();
        verify(internalHandler).flush();
    }

    @Test
    void testCloseIsPropagated(final BetonQuestLogger log) {
        final LazyHandler handler = new LazyHandler(log, () -> internalHandler);
        handler.publish(record);
        handler.close();
        verify(internalHandler).close();
    }

    @Test
    void testRequireNotClosed(final BetonQuestLogger log) {
        final LazyHandler handler = new LazyHandler(log, () -> internalHandler);
        handler.close();
        final Exception exception = assertThrows(IllegalStateException.class, () -> handler.publish(null), "Expected IllegalStateException is thrown");
        assertEquals("Cannot publish log record: LazyLogHandler was closed and had not been initialized before closing.",
                exception.getMessage(), "Expected other exception message");
    }
}
