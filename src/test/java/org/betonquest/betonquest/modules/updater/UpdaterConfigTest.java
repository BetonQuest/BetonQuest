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
    public static final Version VERSION1 = new Version("2.0.0");
    public static final Version VERSION2 = new Version("2.0.0-DEV-1");
    private static final String DEV_INDICATOR = "DEV";

    @SuppressWarnings("PMD.ExcessiveMethodLength")
    private static Stream<Arguments> combinations() {
        return Stream.of(
                Arguments.of(
                        new Input(VERSION1, true, true, MAJOR, true),
                        new Expected(true, UpdateStrategy.MAJOR, false, true, true, false)),
                Arguments.of(
                        new Input(VERSION1, true, true, MAJOR, false),
                        new Expected(true, UpdateStrategy.MAJOR, false, false, true, false)),
                Arguments.of(
                        new Input(VERSION1, true, true, MAJOR_DEV, true),
                        new Expected(true, UpdateStrategy.MAJOR, true, true, true, false)),
                Arguments.of(
                        new Input(VERSION1, true, true, MAJOR_DEV, false),
                        new Expected(true, UpdateStrategy.MAJOR, true, false, true, false)),
                Arguments.of(
                        new Input(VERSION1, true, false, MAJOR, true),
                        new Expected(true, UpdateStrategy.MAJOR, false, true, false, false)),
                Arguments.of(
                        new Input(VERSION1, true, false, MAJOR, false),
                        new Expected(true, UpdateStrategy.MAJOR, false, false, false, false)),
                Arguments.of(
                        new Input(VERSION1, true, false, MAJOR_DEV, true),
                        new Expected(true, UpdateStrategy.MAJOR, true, true, false, false)),
                Arguments.of(
                        new Input(VERSION1, true, false, MAJOR_DEV, false),
                        new Expected(true, UpdateStrategy.MAJOR, true, false, false, false)),
                Arguments.of(
                        new Input(VERSION1, false, true, MAJOR, true),
                        new Expected(false, UpdateStrategy.MAJOR, false, true, true, false)),
                Arguments.of(
                        new Input(VERSION1, false, true, MAJOR, false),
                        new Expected(false, UpdateStrategy.MAJOR, false, false, true, false)),
                Arguments.of(
                        new Input(VERSION1, false, true, MAJOR_DEV, true),
                        new Expected(false, UpdateStrategy.MAJOR, true, true, true, false)),
                Arguments.of(
                        new Input(VERSION1, false, true, MAJOR_DEV, false),
                        new Expected(false, UpdateStrategy.MAJOR, true, false, true, false)),
                Arguments.of(
                        new Input(VERSION1, false, false, MAJOR, true),
                        new Expected(false, UpdateStrategy.MAJOR, false, true, false, false)),
                Arguments.of(
                        new Input(VERSION1, false, false, MAJOR, false),
                        new Expected(false, UpdateStrategy.MAJOR, false, false, false, false)),
                Arguments.of(
                        new Input(VERSION1, false, false, MAJOR_DEV, true),
                        new Expected(false, UpdateStrategy.MAJOR, true, true, false, false)),
                Arguments.of(
                        new Input(VERSION1, false, false, MAJOR_DEV, false),
                        new Expected(false, UpdateStrategy.MAJOR, true, false, false, false)),
                Arguments.of(
                        new Input(VERSION2, true, true, MINOR, true),
                        new Expected(true, UpdateStrategy.MINOR, true, false, true, true)),
                Arguments.of(
                        new Input(VERSION2, true, true, MINOR, false),
                        new Expected(true, UpdateStrategy.MINOR, true, false, true, true)),
                Arguments.of(
                        new Input(VERSION2, true, true, MINOR_DEV, true),
                        new Expected(true, UpdateStrategy.MINOR, true, true, true, false)),
                Arguments.of(
                        new Input(VERSION2, true, true, MINOR_DEV, false),
                        new Expected(true, UpdateStrategy.MINOR, true, false, true, false)),
                Arguments.of(
                        new Input(VERSION2, true, false, MINOR, true),
                        new Expected(true, UpdateStrategy.MINOR, true, false, false, true)),
                Arguments.of(
                        new Input(VERSION2, true, false, MINOR, false),
                        new Expected(true, UpdateStrategy.MINOR, true, false, false, true)),
                Arguments.of(
                        new Input(VERSION2, true, false, MINOR_DEV, true),
                        new Expected(true, UpdateStrategy.MINOR, true, true, false, false)),
                Arguments.of(
                        new Input(VERSION2, true, false, MINOR_DEV, false),
                        new Expected(true, UpdateStrategy.MINOR, true, false, false, false)),
                Arguments.of(
                        new Input(VERSION2, true, false, INVALID, false),
                        new Expected(true, UpdateStrategy.MINOR, true, false, false, true)),
                Arguments.of(
                        new Input(VERSION2, false, true, MINOR, true),
                        new Expected(false, UpdateStrategy.MINOR, true, false, true, true)),
                Arguments.of(
                        new Input(VERSION2, false, true, MINOR, false),
                        new Expected(false, UpdateStrategy.MINOR, true, false, true, true)),
                Arguments.of(
                        new Input(VERSION2, false, true, MINOR_DEV, true),
                        new Expected(false, UpdateStrategy.MINOR, true, true, true, false)),
                Arguments.of(
                        new Input(VERSION2, false, true, MINOR_DEV, false),
                        new Expected(false, UpdateStrategy.MINOR, true, false, true, false)),
                Arguments.of(
                        new Input(VERSION2, false, false, MINOR, true),
                        new Expected(false, UpdateStrategy.MINOR, true, false, false, true)),
                Arguments.of(
                        new Input(VERSION2, false, false, MINOR, false),
                        new Expected(false, UpdateStrategy.MINOR, true, false, false, true)),
                Arguments.of(
                        new Input(VERSION2, false, false, MINOR_DEV, true),
                        new Expected(false, UpdateStrategy.MINOR, true, true, false, false)),
                Arguments.of(
                        new Input(VERSION2, false, false, MINOR_DEV, false),
                        new Expected(false, UpdateStrategy.MINOR, true, false, false, false)),
                Arguments.of(
                        new Input(VERSION2, false, false, INVALID, false),
                        new Expected(false, UpdateStrategy.MINOR, true, false, false, true))
        );
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void testUpdaterConfig(final Input input, final Expected expected) {
        final ConfigurationFile config = getMockedConfig(input);
        final UpdaterConfig updaterConfig = new UpdaterConfig(config, input.version, DEV_INDICATOR);
        assertSettings(expected, updaterConfig);
    }

    private ConfigurationFile getMockedConfig(final Input input) {
        final ConfigurationFile config = mock(ConfigurationFile.class);
        when(config.getBoolean("enabled", true)).thenReturn(input.enabled);
        when(config.getBoolean("ingameNotification", true)).thenReturn(input.ingameNotification);
        when(config.getString("strategy", MINOR)).thenReturn(input.strategy);
        when(config.getBoolean("automatic", false)).thenReturn(input.automatic);
        return config;
    }

    private void assertSettings(final Expected expected, final UpdaterConfig updaterConfig) {
        assertEquals(expected.enabled, updaterConfig.isEnabled(), "Expected isEnabled is '" + expected.enabled + "'");
        assertEquals(expected.strategy, updaterConfig.getStrategy(), "Expected getStrategy is '" + expected.strategy + "'");
        assertEquals(expected.devDownloadEnabled, updaterConfig.isDevDownloadEnabled(), "Expected isDevDownloadEnabled is '" + expected.devDownloadEnabled + "'");
        assertEquals(expected.automatic, updaterConfig.isAutomatic(), "Expected isAutomatic is '" + expected.automatic + "'");
        assertEquals(expected.ingameNotification, updaterConfig.isIngameNotification(), "Expected isIngameNotification is '" + expected.ingameNotification + "'");
        assertEquals(expected.forcedStrategy, updaterConfig.isForcedStrategy(), "Expected isForcedStrategy is '" + expected.forcedStrategy + "'");
    }

    private record Input(Version version, boolean enabled, boolean ingameNotification, String strategy,
                         boolean automatic) {
    }

    private record Expected(boolean enabled, UpdateStrategy strategy, boolean devDownloadEnabled, boolean automatic,
                            boolean ingameNotification, boolean forcedStrategy) {
    }
}
