package org.betonquest.betonquest.lib.config.section.unmodifiable;

import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base abstract test class for {@link UnmodifiableConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public abstract class AbstractUnmodifiableConfigurationSectionTest extends ConfigurationSectionBaseTest {

    /**
     * The values in the configuration before the test was run.
     */
    private Map<String, Object> values;

    /**
     * The values of the default section in the configuration before the test was run.
     */
    private Map<String, Object> valuesDefault;

    public AbstractUnmodifiableConfigurationSectionTest(final ConfigurationSection config) {
        super(config);
    }

    /**
     * Get a copy of the values in the config, before the test runs.
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
    public void create_section_with_values_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::create_section_with_values_on_existing_config_path);
    }

    @Test
    @Override
    public void add_default() {
        assertThrowsUnmodifiableException(super::add_default);
    }

    @Test
    @Override
    public void set() {
        assertThrowsUnmodifiableException(super::set);
    }

    @Test
    @Override
    public void set_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::set_on_existing_config_path);
    }

    @Test
    @Override
    public void set_section_overwriting_existing() {
        assertThrowsUnmodifiableException(super::set_section_overwriting_existing);
    }

    @Test
    @Override
    public void set_existing_section_to_null() {
        assertThrowsUnmodifiableException(super::set_existing_section_to_null);
    }

    @Test
    @Override
    public void create_section_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::create_section_on_existing_config_path);
    }

    @Test
    @Override
    public void create_section() {
        assertThrowsUnmodifiableException(super::create_section);
    }

    @Test
    @Override
    public void add_default_on_existing_config_path() {
        assertThrowsUnmodifiableException(super::add_default_on_existing_config_path);
    }

    @Test
    @Override
    public void create_section_with_values() {
        assertThrowsUnmodifiableException(super::create_section_with_values);
    }

    @Test
    @Override
    public void set_comments() {
        assertThrowsUnmodifiableException(super::set_comments);
    }

    @Test
    @Override
    public void set_comments_on_invalid() {
        assertThrowsUnmodifiableException(super::set_comments_on_invalid);
    }

    @Test
    @Override
    public void set_inline_comments() {
        assertThrowsUnmodifiableException(super::set_inline_comments);
    }

    @Test
    @Override
    public void set_inline_comments_on_invalid() {
        assertThrowsUnmodifiableException(super::set_inline_comments_on_invalid);
    }
}
