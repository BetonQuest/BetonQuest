package org.betonquest.betonquest.lib.config.section.unmodifiable;

import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
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
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public class UnmodifiableConfigurationTest extends ConfigurationBaseTest {

    /**
     * The values in the configuration before the test did run.
     */
    private Map<String, Object> values;

    /**
     * The values of the default section in the configuration before the test did run.
     */
    private Map<String, Object> valuesDefault;

    public UnmodifiableConfigurationTest() {
        super(new UnmodifiableConfiguration(getDefaultConfig()));
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
    public void add_default_on_root_section() {
        assertThrowsUnmodifiableException(super::add_default_on_root_section);
    }

    @Test
    @Override
    public void add_default_on_root_section_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::add_default_on_root_section_on_existing_config_path);
    }

    @Test
    @Override
    public void add_defaults_as_configuration() {
        assertThrowsUnmodifiableException(super::add_defaults_as_configuration);
    }

    @Test
    @Override
    public void add_defaults_as_configuration_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::add_defaults_as_configuration_on_existing_config_path);
    }

    @Test
    @Override
    public void add_defaults_as_map() {
        assertThrowsUnmodifiableException(super::add_defaults_as_map);
    }

    @Test
    @Override
    public void add_defaults_as_map_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::add_defaults_as_map_on_existing_config_path);
    }

    @Test
    @Override
    public void set_defaults() {
        assertThrowsUnmodifiableException(super::set_defaults);
    }

    @Test
    @Override
    public void set_defaults_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::set_defaults_on_existing_config_path);
    }
}
