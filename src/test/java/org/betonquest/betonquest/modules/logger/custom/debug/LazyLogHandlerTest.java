package org.betonquest.betonquest.modules.logger.custom.debug;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for a {@link LazyLogHandler}.
 */
class LazyLogHandlerTest {

    /**
     * Default constructor.
     */
    public LazyLogHandlerTest() {
        // Empty
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void test() {
        final AtomicBoolean created = new AtomicBoolean(false);
        final ResettableHandler handler = new LazyLogHandler(() -> {
            created.set(true);
            return Mockito.mock(Handler.class);
        });
        assertFalse(created.get(), "Should not be created yet");
        handler.publish(null);
        assertTrue(created.get(), "Should be created");
        handler.reset();
        created.set(false);
        handler.publish(null);
        assertTrue(created.get(), "Should be created");
    }
}
