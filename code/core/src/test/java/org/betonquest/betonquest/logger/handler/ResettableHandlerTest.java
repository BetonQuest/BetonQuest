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
    void publish_passes_through(@Mock final Handler internal) {
        final LogRecord record = new LogRecord(Level.INFO, "message");
        final ResettableHandler handler = new ResettableHandler(() -> internal);

        handler.publish(record);

        verify(internal).publish(record);
    }

    @Test
    void flush_passes_through(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        handler.flush();
        verify(internal).flush();
    }

    @Test
    void internal_handler_is_eagerly_loaded(@Mock final Handler internal, @Mock final Supplier<Handler> supplier) {
        when(supplier.get()).thenReturn(internal);
        new ResettableHandler(supplier);
        verify(supplier).get();
    }

    @Test
    void reset_closes_old_handler(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        verifyNoInteractions(internal);
        handler.reset();
        verify(internal).close();
    }

    @Test
    void reset_fetches_new_handler(@Mock final Handler internal, @Mock final Supplier<Handler> supplier) {
        when(supplier.get()).thenReturn(internal);
        final ResettableHandler handler = new ResettableHandler(supplier);

        handler.reset();

        verify(supplier, times(2)).get();
    }

    @Test
    void correct_handler_is_used_after_reset(@Mock final Handler handlerBeforeReset, @Mock final Handler handlerAfterReset) {
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
    void close_closes_underlying_handler(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        verifyNoInteractions(internal);
        handler.close();
        verify(internal).close();
    }

    @Test
    void reset_fails_after_close(@Mock final Handler internal) {
        final ResettableHandler handler = new ResettableHandler(() -> internal);
        handler.close();

        assertThrows(IllegalStateException.class, handler::reset, "Resetting a closed handler should fail.");
    }
}
