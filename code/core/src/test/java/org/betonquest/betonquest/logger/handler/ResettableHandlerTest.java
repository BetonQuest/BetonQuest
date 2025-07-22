package org.betonquest.betonquest.logger.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test {@link ResettableHandler}.
 */
@ExtendWith(MockitoExtension.class)
class ResettableHandlerTest {

    @Test
    void testPublishPassesThrough(@Mock final Handler internal) {
        final LogRecord record = new LogRecord(Level.INFO, "message");
        final ResettableHandler handler = new ResettableHandler(() -> internal);

        handler.publish(record);

        verify(internal).publish(record);
    }

    @Test
    void testFlushPassesThrough(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        handler.flush();
        verify(internal).flush();
    }

    @Test
    void testInternalHandlerIsEagerlyLoaded(@Mock final Handler internal, @Mock final Supplier<Handler> supplier) {
        when(supplier.get()).thenReturn(internal);
        new ResettableHandler(supplier);
        verify(supplier).get();
    }

    @Test
    void testResetClosesOldHandler(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        verifyNoInteractions(internal);
        handler.reset();
        verify(internal).close();
    }

    @Test
    void testResetFetchesNewHandler(@Mock final Handler internal, @Mock final Supplier<Handler> supplier) {
        when(supplier.get()).thenReturn(internal);
        final ResettableHandler handler = new ResettableHandler(supplier);

        handler.reset();

        verify(supplier, times(2)).get();
    }

    @Test
    void testCorrectHandlerIsUsedAfterReset(@Mock final Handler handlerBeforeReset, @Mock final Handler handlerAfterReset) {
        final Iterator<Handler> supplier = List.of(handlerBeforeReset, handlerAfterReset).iterator();

        final ResettableHandler handler = new ResettableHandler(supplier::next);

        final LogRecord firstRecord = new LogRecord(Level.INFO, "first");
        handler.publish(firstRecord);
        handler.reset();

        verifyNoInteractions(handlerAfterReset);
        final LogRecord secondRecord = new LogRecord(Level.WARNING, "second");
        handler.publish(secondRecord);
        verify(handlerAfterReset).publish(secondRecord);
    }

    @Test
    void testCloseClosesUnderlyingHandler(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        verifyNoInteractions(internal);
        handler.close();
        verify(internal).close();
    }

    @Test
    void testResetFailsAfterClose(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        handler.close();

        assertThrows(IllegalStateException.class, handler::reset, "Resetting a closed handler should fail.");
    }
}
