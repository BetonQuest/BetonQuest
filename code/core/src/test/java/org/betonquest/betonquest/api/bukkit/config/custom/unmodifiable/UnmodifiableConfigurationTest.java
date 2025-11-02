package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link UnmodifiableConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class UnmodifiableConfigurationTest extends ConfigurationBaseTest {
    /**
     * The values in the configuration before the test did run.
     */
    private Map<String, Object> values;

    /**
     * The values of the default section in the configuration before the test did run.
     */
    private Map<String, Object> valuesDefault;

    @Override
    public Configuration getConfig() {
        return new UnmodifiableConfiguration(super.getDefaultConfig());
    }

    /**
     * Get a copy of the values in the config, before the test did run.
     */
    @BeforeEach
    public void savePreviousValues() {
        values = config.getValues(true);
        valuesDefault = Objects.requireNonNull(config.getDefaultSection()).getValues(true);
    }

    /**
     * Compare the start values with the values after the test.
     * They should not have been changed.
     */
    @AfterEach
    public void assertNotModified() {
        assertEquals(values.toString(), config.getValues(true).toString());
        assertEquals(valuesDefault.toString(), Objects.requireNonNull(config.getDefaultSection()).getValues(true).toString());
    }

    private void assertThrowsUnmodifiableException(final Executable executable) {
        final Exception exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals(UnmodifiableConfiguration.UNMODIFIABLE_MESSAGE, exception.getMessage());
    }

    @Test
    @Override
    public void testAddDefaultOnRootSection() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnRootSection);
    }

    @Test
    @Override
    public void testAddDefaultOnRootSectionOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultOnRootSectionOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfiguration() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsConfiguration);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfigurationOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsConfigurationOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefaultsAsMap() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsMap);
    }

    @Test
    @Override
    public void testAddDefaultsAsMapOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testAddDefaultsAsMapOnExistingConfigPath);
    }

    @Test
    @Override
    public void testSetDefaults() {
        assertThrowsUnmodifiableException(super::testSetDefaults);
    }

    @Test
    @Override
    public void testSetDefaultsOnExistingConfigPath() {
        assertThrowsUnmodifiableException(super::testSetDefaultsOnExistingConfigPath);
    }
}
