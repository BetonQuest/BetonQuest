package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnitAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationSectionTest extends ConfigurationSectionBaseTest {
    /**
     * The fallback {@link Configuration that should not be modified.
     */
    protected Configuration fallback;

    /**
     * The values in the fallback configuration before the test did run.
     */
    private Map<String, Object> values;

    /**
     * The values of the default section in the fallback configuration before the test did run.
     */
    private Map<String, Object> valuesDefault;

    @Override
    public ConfigurationSection getConfig() {
        final Configuration original = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/fallback/original.yml"));
        fallback = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/fallback/fallback.yml"));

        final Configuration defaults = super.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        return new FallbackConfiguration(original, fallback);
    }

    /**
     * Get a copy of the values in the config, before the test did run.
     */
    @BeforeEach
    public void savePreviousValues() {
        values = fallback.getValues(true);
        final ConfigurationSection defaultSection = fallback.getDefaultSection();
        valuesDefault = defaultSection == null ? null : defaultSection.getValues(true);
    }

    /**
     * Compare the start values with the values after the test.
     * They should not have been changed.
     */
    @AfterEach
    public void assertNotModified() {
        assertEquals(values, fallback.getValues(true));
        final ConfigurationSection defaultSection = fallback.getDefaultSection();
        assertEquals(valuesDefault, defaultSection == null ? null : defaultSection.getValues(true));
    }
}
