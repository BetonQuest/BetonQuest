package org.betonquest.betonquest.modules.updater;

import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.versioning.UpdateStrategy;
import org.betonquest.betonquest.modules.versioning.Version;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
final class UpdaterConfigTest {
    public static final String INVALID = "INVALID";
    public static final String MINOR_DEV = "MINOR_DEV";
    public static final String MINOR = "MINOR";
    public static final String MAJOR_DEV = "MAJOR_DEV";
    public static final String MAJOR = "MAJOR";
    public static final String VERSION2 = "2.0.0-DEV-1";
    public static final String VERSION1 = "2.0.0";
    private static final String DEV_INDICATOR = "DEV";

    private UpdaterConfigTest() {
        // Empty
    }

    private static Stream<Arguments> combinations() {
        return Stream.of(
                Arguments.of(new Version(VERSION1), true, true, MAJOR, true,
                        true, UpdateStrategy.MAJOR, false, true, true, false),
                Arguments.of(new Version(VERSION1), true, true, MAJOR, false,
                        true, UpdateStrategy.MAJOR, false, false, true, false),
                Arguments.of(new Version(VERSION1), true, true, MAJOR_DEV, true,
                        true, UpdateStrategy.MAJOR, true, true, true, false),
                Arguments.of(new Version(VERSION1), true, true, MAJOR_DEV, false,
                        true, UpdateStrategy.MAJOR, true, false, true, false),
                Arguments.of(new Version(VERSION1), true, false, MAJOR, true,
                        true, UpdateStrategy.MAJOR, false, true, false, false),
                Arguments.of(new Version(VERSION1), true, false, MAJOR, false,
                        true, UpdateStrategy.MAJOR, false, false, false, false),
                Arguments.of(new Version(VERSION1), true, false, MAJOR_DEV, true,
                        true, UpdateStrategy.MAJOR, true, true, false, false),
                Arguments.of(new Version(VERSION1), true, false, MAJOR_DEV, false,
                        true, UpdateStrategy.MAJOR, true, false, false, false),
                Arguments.of(new Version(VERSION2), false, true, MINOR, true,
                        false, UpdateStrategy.MINOR, true, false, true, true),
                Arguments.of(new Version(VERSION2), false, true, MINOR, false,
                        false, UpdateStrategy.MINOR, true, false, true, true),
                Arguments.of(new Version(VERSION2), false, true, MINOR_DEV, true,
                        false, UpdateStrategy.MINOR, true, true, true, false),
                Arguments.of(new Version(VERSION2), false, true, MINOR_DEV, false,
                        false, UpdateStrategy.MINOR, true, false, true, false),
                Arguments.of(new Version(VERSION2), false, false, MINOR, true,
                        false, UpdateStrategy.MINOR, true, false, false, true),
                Arguments.of(new Version(VERSION2), false, false, MINOR, false,
                        false, UpdateStrategy.MINOR, true, false, false, true),
                Arguments.of(new Version(VERSION2), false, false, MINOR_DEV, true,
                        false, UpdateStrategy.MINOR, true, true, false, false),
                Arguments.of(new Version(VERSION2), false, false, MINOR_DEV, false,
                        false, UpdateStrategy.MINOR, true, false, false, false),
                Arguments.of(new Version(VERSION2), false, false, INVALID, false,
                        false, UpdateStrategy.MINOR, true, false, false, true)
        );
    }

    @ParameterizedTest
    @MethodSource("combinations")
    @SuppressWarnings("PMD.ExcessiveParameterList")
    void testUpdaterConfig(final Version version, final boolean enabled, final boolean ingameNotification,
                           final String strategy, final boolean automatic, final boolean expectedEnabled,
                           final UpdateStrategy expectedStrategy, final boolean expectedDevDownloadEnabled,
                           final boolean expectedAutomatic, final boolean expectedIngameNotification,
                           final boolean expectedForcedStrategy) {
        final ConfigurationFile config = getMockedConfig(enabled, ingameNotification, strategy, automatic);
        final UpdaterConfig updaterConfig = new UpdaterConfig(config, version, DEV_INDICATOR);
        assertSettings(expectedEnabled, expectedStrategy, expectedDevDownloadEnabled, expectedAutomatic, expectedIngameNotification, expectedForcedStrategy, updaterConfig);
    }

    private ConfigurationFile getMockedConfig(final boolean enabled, final boolean ingameNotification,
                                              final String strategy, final boolean automatic) {
        final ConfigurationFile config = mock(ConfigurationFile.class);
        when(config.getBoolean("update.enabled", true)).thenReturn(enabled);
        when(config.getBoolean("update.ingameNotification", true)).thenReturn(ingameNotification);
        when(config.getString("update.strategy", MINOR)).thenReturn(strategy);
        when(config.getBoolean("update.automatic", false)).thenReturn(automatic);
        return config;
    }

    private void assertSettings(final boolean expectedEnabled, final UpdateStrategy expectedStrategy, final boolean expectedDevDownloadEnabled, final boolean expectedAutomatic, final boolean expectedIngameNotification, final boolean expectedForcedStrategy, final UpdaterConfig updaterConfig) {
        assertEquals(expectedEnabled, updaterConfig.isEnabled(), "Expected isEnabled is '" + expectedEnabled + "'");
        assertEquals(expectedStrategy, updaterConfig.getStrategy(), "Expected getStrategy is '" + expectedStrategy + "'");
        assertEquals(expectedDevDownloadEnabled, updaterConfig.isDevDownloadEnabled(), "Expected isDevDownloadEnabled is '" + expectedDevDownloadEnabled + "'");
        assertEquals(expectedAutomatic, updaterConfig.isAutomatic(), "Expected isAutomatic is '" + expectedAutomatic + "'");
        assertEquals(expectedIngameNotification, updaterConfig.isIngameNotification(), "Expected isIngameNotification is '" + expectedIngameNotification + "'");
        assertEquals(expectedForcedStrategy, updaterConfig.isForcedStrategy(), "Expected isForcedStrategy is '" + expectedForcedStrategy + "'");
    }
}
