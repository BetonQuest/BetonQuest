package org.betonquest.betonquest.modules.logger.custom.debug.config;

import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for a {@link FileDebugConfig} based on {@link SimpleDebugConfig}.
 */
class FileDebugConfigTest extends SimpleDebugConfigTest {
    /**
     * Temporary directory for testing.
     */
    @TempDir
    private File tempDir;

    /**
     * Default constructor.
     */
    public FileDebugConfigTest() {
        super();
    }

    @Test
    @Override
    void testEnableDisable() throws IOException {
        final ConfigurationFile configurationFile = mock(ConfigurationFile.class);
        when(configurationFile.getBoolean("debug.enabled", false)).thenReturn(false);
        final DebugConfig debugConfig = new FileDebugConfig(configurationFile, tempDir);
        assertTestEnableDisable(debugConfig);
    }

    @Test
    @Override
    void testDefaultEnabled() {
        final ConfigurationFile configurationFile = mock(ConfigurationFile.class);
        when(configurationFile.getBoolean("debug.enabled", false)).thenReturn(true);
        final DebugConfig debugConfig = new FileDebugConfig(configurationFile, tempDir);
        assertTrue(debugConfig.isDebugging(), "Debugging should be enabled by default");
    }
}
