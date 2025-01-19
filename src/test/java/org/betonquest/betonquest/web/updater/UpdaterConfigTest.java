package org.betonquest.betonquest.web.updater;

import org.betonquest.betonquest.api.config.ConfigurationFile;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link UpdaterConfig}.
 */
final class UpdaterConfigTest {
    /**
     * Invalid {@link UpdateStrategy}
     */
    public static final String INVALID = "INVALID";

    /**
     * {@link UpdateStrategy#MINOR}_DEV {@link UpdateStrategy}
     */
    public static final String MINOR_DEV = "MINOR_DEV";

    /**
     * {@link UpdateStrategy#MINOR} {@link UpdateStrategy}
     */
    public static final String MINOR = "MINOR";

    /**
     * {@link UpdateStrategy#MAJOR}_DEV {@link UpdateStrategy}
     */
    public static final String MAJOR_DEV = "MAJOR_DEV";

    /**
     * {@link UpdateStrategy#MAJOR} {@link UpdateStrategy}
     */
    public static final String MAJOR = "MAJOR";

    /**
     * 2.0.0 {@link Version}
     */
    public static final Version VERSION1 = new Version("2.0.0");

    /**
     * 2.0.0-DEV-1 {@link Version}
     */
    public static final Version VERSION2 = new Version("2.0.0-DEV-1");

    /**
     * DEV indicator for versions
     */
    private static final String DEV_INDICATOR = "DEV";

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
                        new Expected(false, UpdateStrategy.MINOR, true, false, false, true)),
                // Special cases
                Arguments.of(
                        new Input(VERSION2, true, false, "PATCH", false),
                        new Expected(true, UpdateStrategy.PATCH, true, false, false, true))

        );
    }

    @SuppressWarnings("PMD.CommentDefaultAccessModifier")
    static UpdaterConfig getMockedConfig(final BetonQuestLogger logger, final Input input, final Version version) {
        final ConfigurationFile config = mock(ConfigurationFile.class);
        when(config.getBoolean("update.enabled", true)).thenReturn(input.enabled);
        when(config.getBoolean("update.ingameNotification", true)).thenReturn(input.ingameNotification);
        when(config.getString("update.strategy", null)).thenReturn(input.strategy);
        when(config.getBoolean("update.automatic", false)).thenReturn(input.automatic);
        return new UpdaterConfig(logger, config, version, DEV_INDICATOR);
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void testUpdaterConfig(final Input input, final Expected expected) {
        final UpdaterConfig updaterConfig = getMockedConfig(mock(BetonQuestLogger.class), input, input.version);
        updaterConfig.reloadFromConfig();
        assertSettings(expected, updaterConfig);
    }

    private void assertSettings(final Expected expected, final UpdaterConfig updaterConfig) {
        assertEquals(expected.enabled, updaterConfig.isEnabled(), "Expected isEnabled is '" + expected.enabled + "'");
        assertEquals(expected.strategy, updaterConfig.getStrategy(), "Expected getStrategy is '" + expected.strategy + "'");
        assertEquals(expected.devDownloadEnabled, updaterConfig.isDevDownloadEnabled(), "Expected isDevDownloadEnabled is '" + expected.devDownloadEnabled + "'");
        assertEquals(expected.automatic, updaterConfig.isAutomatic(), "Expected isAutomatic is '" + expected.automatic + "'");
        assertEquals(expected.ingameNotification, updaterConfig.isIngameNotification(), "Expected isIngameNotification is '" + expected.ingameNotification + "'");
        assertEquals(expected.forcedStrategy, updaterConfig.isForcedStrategy(), "Expected isForcedStrategy is '" + expected.forcedStrategy + "'");
    }

    /**
     * Utility record for input values.
     *
     * @param version            version
     * @param enabled            enabled
     * @param ingameNotification ingameNotification
     * @param strategy           strategy
     * @param automatic          automatic
     */
    /* default */ record Input(Version version, boolean enabled, boolean ingameNotification, String strategy,
                               boolean automatic) {
    }

    /**
     * Utility record for expected values.
     *
     * @param enabled            enabled
     * @param strategy           strategy
     * @param devDownloadEnabled devDownloadEnabled
     * @param automatic          automatic
     * @param ingameNotification ingameNotification
     * @param forcedStrategy     forcedStrategy
     */
    private record Expected(boolean enabled, UpdateStrategy strategy, boolean devDownloadEnabled, boolean automatic,
                            boolean ingameNotification, boolean forcedStrategy) {
    }
}
