package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.InvalidSubConfigurationException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.KeyConflictException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiSectionConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.ibm.icu.impl.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiSectionConfiguration} and it's thrown {@link KeyConflictException}s.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
class MultiFallbackConfigurationDoNotEffectEachOtherTest {
    /**
     * Config one with a schedule
     */
    public static final String CONFIG_STRING_1 = """
            schedules:
                animation_reset:
                    type: realtime-cron
                    time: '@reboot'
                    events: animation_reset
            """;

    /**
     * Config two with a schedule
     */
    public static final String CONFIG_STRING_2 = """
            schedules:
                player_detector_reset:
                    type: realtime-cron
                    time: '@reboot'
                    events: player_detector_reset
            """;

    /**
     * Config with an implementation of the schedule event
     */
    public static final String CONFIG_STRING_3 = """
            events:
                player_detector_reset: test event
            """;

    @Test
    void after_merge_one_no_keys_should_be_in_merge_to_related_to_merge_one() throws KeyConflictException, InvalidSubConfigurationException {
        final MultiSectionConfiguration configTemplate1 = new MultiSectionConfiguration(List.of(createConfig(CONFIG_STRING_1)));
        final MultiSectionConfiguration configTemplate2 = new MultiSectionConfiguration(List.of(createConfig(CONFIG_STRING_2)));
        final MultiSectionConfiguration config = new MultiSectionConfiguration(List.of(createConfig(CONFIG_STRING_3)));

        new MultiFallbackConfiguration(configTemplate2, configTemplate1);
        final MultiFallbackConfiguration multiConfig = new MultiFallbackConfiguration(config, configTemplate1);

        assertEquals(multiConfig.getKeys(true), Set.of(
                        "schedules.animation_reset.events",
                        "schedules.animation_reset.time",
                        "schedules.animation_reset",
                        "schedules",
                        "schedules.animation_reset.type",
                        "events"),
                "Keys are not equal");
    }

    private ConfigurationSection createConfig(final String configStrings) {
        final YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(configStrings);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return config;
    }
}
