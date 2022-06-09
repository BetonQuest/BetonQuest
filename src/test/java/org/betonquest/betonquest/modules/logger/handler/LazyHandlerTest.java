package org.betonquest.betonquest.modules.logger.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.mockito.Mockito.*;

/**
 * Test for a {@link LazyHandler}.
 */
@ExtendWith(MockitoExtension.class)
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

    /**
     * Default constructor.
     */
    public LazyHandlerTest() {
    }

    @Test
    void testLazyInstantiation(@Mock final Supplier<Handler> handlerSupplier) {
        when(handlerSupplier.get()).thenReturn(internalHandler);
        final LazyHandler handler = new LazyHandler(handlerSupplier);
        verify(handlerSupplier, never()).get();
        handler.publish(record);
        verify(handlerSupplier).get();
    }

    @Test
    void testFlushDoesNotCauseInitialization(@Mock final Supplier<Handler> handlerSupplier) {
        final LazyHandler handler = new LazyHandler(handlerSupplier);
        handler.flush();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testCloseDoesNotCauseInitialization(@Mock final Supplier<Handler> handlerSupplier) {
        final LazyHandler handler = new LazyHandler(handlerSupplier);
        handler.close();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testPublishAfterClosingDoesNotCauseInitialization(@Mock final Supplier<Handler> handlerSupplier) {
        final LazyHandler handler = new LazyHandler(handlerSupplier);
        handler.close();
        verify(handlerSupplier, never()).get();
    }

    @Test
    void testPublishIsPropagated() {
        final LazyHandler handler = new LazyHandler(() -> internalHandler);
        handler.publish(record);
        verify(internalHandler).publish(record);
    }

    @Test
    void testFlushIsPropagated() {
        final LazyHandler handler = new LazyHandler(() -> internalHandler);
        handler.publish(record);
        handler.flush();
        verify(internalHandler).flush();
    }

    @Test
    void testCloseIsPropagated() {
        final LazyHandler handler = new LazyHandler(() -> internalHandler);
        handler.publish(record);
        handler.close();
        verify(internalHandler).close();
    }
}
