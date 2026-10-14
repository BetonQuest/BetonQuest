package org.betonquest.betonquest.lib.config.section.fallback;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base abstract test class for {@link FallbackConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings("PMD.UnitTestAssertionsShouldIncludeMessage")
public abstract class AbstractFallbackConfigurationSectionTest extends ConfigurationSectionBaseTest {

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
    @Nullable
    private Map<String, Object> valuesDefault;

    public AbstractFallbackConfigurationSectionTest(final ConfigurationSection config, final Configuration fallback) {
        super(config);
        this.fallback = fallback;
    }

    public static Configuration getDefaultConfig(final Configuration fallback) {
        final Configuration original = setupOriginal();

        final Configuration defaults = ConfigurationSectionBaseTest.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        return new FallbackConfiguration(original, fallback);
    }

    /**
     * Get the original {@link ConfigurationSection}.
     *
     * @return The original {@link ConfigurationSection}
     */
    protected ConfigurationSection getOriginal() {
        return ((FallbackConfigurationSection) config).manager.getOriginal();
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
