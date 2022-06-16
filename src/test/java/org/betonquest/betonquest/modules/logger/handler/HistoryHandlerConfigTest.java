package org.betonquest.betonquest.modules.logger.handler;

import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.handler.history.HistoryHandlerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for the {@link HistoryHandlerConfig}.
 */
@ExtendWith(MockitoExtension.class)
class HistoryHandlerConfigTest {
    /**
     * Path where to expect the debug config value.
     */
    public static final String DEBUG_ACTIVE_CONFIG_PATH = "debug.enabled";
    /**
     * Configuration file to use.
     */
    @Mock
    private ConfigurationFile configurationFile;

    /**
     * Directory to write logs to.
     */
    @TempDir
    private File logDirectory;

    /**
     * Default constructor.
     */
    public HistoryHandlerConfigTest() {
        super();
    }

    static Stream<Arguments> debugValueChanges() {
        return Stream.of(
                Arguments.of(false, false),
                Arguments.of(false, true),
                Arguments.of(true, false),
                Arguments.of(true, true)
        );
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testInitialValueIsReadFromConfig(final boolean initialValue) {
        when(configurationFile.getBoolean(eq(DEBUG_ACTIVE_CONFIG_PATH), anyBoolean())).thenReturn(initialValue);
        final HistoryHandlerConfig historyHandlerConfig = new HistoryHandlerConfig(configurationFile, logDirectory);
        assertEquals(initialValue, historyHandlerConfig.isLogging(), "Initial value was not successfully loaded");
    }

    @ParameterizedTest
    @MethodSource("debugValueChanges")
    void testToggleDebugging(final boolean initialValue, final boolean newValue) throws IOException {
        when(configurationFile.getBoolean(eq(DEBUG_ACTIVE_CONFIG_PATH), anyBoolean())).thenReturn(initialValue);
        final HistoryHandlerConfig historyHandlerConfig = new HistoryHandlerConfig(configurationFile, logDirectory);
        historyHandlerConfig.setLogging(newValue);
        assertEquals(newValue, historyHandlerConfig.isLogging(), "Initial value was not successfully loaded");
        verify(configurationFile).set(DEBUG_ACTIVE_CONFIG_PATH, newValue);
    }
}
