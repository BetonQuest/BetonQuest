package org.betonquest.betonquest.modules.logger.custom.debug.config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for a {@link SimpleDebugConfig}.
 */
class SimpleDebugConfigTest {
    /**
     * Assertion message when order is not correct.
     */
    public static final String EXPECTED_COUNT_MESSAGE = "Expected correct order checked by this counter";

    /**
     * Default constructor.
     */
    public SimpleDebugConfigTest() {
        // Empty
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestContainsTooManyAsserts")
    void testEnableDisable() throws IOException {
        final DebugConfig debugConfig = new SimpleDebugConfig(false);
        assertTestEnableDisable(debugConfig);
    }

    /**
     * Method to test the behaviour of a {@link DebugConfig} implementation,
     *
     * @param debugConfig The {@link DebugConfig} implementation
     * @throws IOException Is eventually thrown, if something unexpected goes wrong
     */
    protected void assertTestEnableDisable(final DebugConfig debugConfig) throws IOException {
        assertFalse(debugConfig.isDebugging(), "Debugging should be disabled");

        final AtomicInteger start = new AtomicInteger(0);
        debugConfig.addOnStartHandler(this, createPrePostRunnable(start));
        final AtomicInteger stop = new AtomicInteger(0);
        debugConfig.addOnStopHandler(this, createPrePostRunnable(stop));

        assertFalse(debugConfig.isDebugging(), "Debugging should be disabled");
        assertEquals(0, start.get(), EXPECTED_COUNT_MESSAGE);
        assertEquals(0, stop.get(), EXPECTED_COUNT_MESSAGE);
        start.set(1);
        debugConfig.startDebug();
        assertTrue(debugConfig.isDebugging(), "Debugging should be enabled");
        assertEquals(4, start.get(), EXPECTED_COUNT_MESSAGE);
        assertEquals(0, stop.get(), EXPECTED_COUNT_MESSAGE);
        stop.set(1);
        debugConfig.stopDebug();
        assertFalse(debugConfig.isDebugging(), "Debugging should be disabled");
        assertEquals(4, start.get(), EXPECTED_COUNT_MESSAGE);
        assertEquals(4, stop.get(), EXPECTED_COUNT_MESSAGE);

        start.set(-1);
        stop.set(-1);
        debugConfig.removeOnStartHandler(this);
        debugConfig.removeOnStopHandler(this);
        debugConfig.startDebug();
        debugConfig.stopDebug();
        assertEquals(-1, start.get(), EXPECTED_COUNT_MESSAGE);
        assertEquals(-1, stop.get(), EXPECTED_COUNT_MESSAGE);
    }

    private DebugConfig.PrePostRunnable createPrePostRunnable(final AtomicInteger integer) {
        return new DebugConfig.PrePostRunnable() {
            @Override
            public void preRun() {
                assertEquals(1, integer.get(), EXPECTED_COUNT_MESSAGE);
                integer.set(2);
            }

            @Override
            public void postRun() {
                assertEquals(3, integer.get(), EXPECTED_COUNT_MESSAGE);
                integer.set(4);
            }

            @Override
            public void run() {
                assertEquals(2, integer.get(), EXPECTED_COUNT_MESSAGE);
                integer.set(3);
            }
        };
    }

    @Test
    void testDefaultEnabled() {
        final DebugConfig debugConfig = new SimpleDebugConfig(true);
        assertTrue(debugConfig.isDebugging(), "Debugging should be enabled by default");
    }
}
