package org.betonquest.betonquest.api.bukkit.config.custom.fallback;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfiguration} class.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.TestClassWithoutTestCases"})
public class FallbackConfigurationTest extends ConfigurationBaseTest {
    /**
     * The fallback {@link Configuration} that should not be modified.
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
    public Configuration getConfig() throws InvalidConfigurationException {
        final Configuration original = setupOriginal();
        fallback = setupFallback();

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
