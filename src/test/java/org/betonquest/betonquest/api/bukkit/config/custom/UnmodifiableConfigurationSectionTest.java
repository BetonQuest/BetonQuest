package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link UnmodifiableConfigurationSection}.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
public class UnmodifiableConfigurationSectionTest extends AbstractConfigurationSectionTest {
    /**
     * The values in the configuration before the test did run.
     */
    private Map<String, Object> values;
    /**
     * The values of the default section in the configuration before the test did run.
     */
    private Map<String, Object> valuesDefault;

    /**
     * Empty constructor
     */
    public UnmodifiableConfigurationSectionTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfigurationSection(super.getDefaultConfig());
    }

    /**
     * Get a copy of the values in the config, before the test did run.
     */
    @BeforeEach
    public void beforeEach() {
        values = config.getValues(true);
        valuesDefault = Objects.requireNonNull(config.getDefaultSection()).getValues(true);
    }

    /**
     * Compare the start values with the values after the test.
     * They should not have been changed.
     */
    @AfterEach
    public void afterEach() {
        assertEquals(values, config.getValues(true));
        assertEquals(valuesDefault, Objects.requireNonNull(config.getDefaultSection()).getValues(true));
    }

    private void assertThrowsUnmodifiableException(final Executable executable) {
        final Exception exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals("This config is unmodifiable", exception.getMessage());
    }

    @Test
    @Override
    public void testCreateSectionWithValuesOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testCreateSectionWithValuesOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefault() {
        assertThrowsUnmodifiableException(super::testAddDefault);
    }

    @Test
    @Override
    public void testSet() {
        assertThrowsUnmodifiableException(super::testSet);
    }

    @Test
    @Override
    public void testSetOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testSetOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSectionOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testCreateSectionOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSection() {
        assertThrowsUnmodifiableException(super::testCreateSection);
    }

    @Test
    @Override
    public void testAddDefaultOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSectionWithValues() {
        assertThrowsUnmodifiableException(super::testCreateSectionWithValues);
    }
}

