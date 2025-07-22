package org.betonquest.betonquest.api.bukkit.config.custom.unmodifiable;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
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
 * This is a test for the {@link UnmodifiableConfiguration} as a {@link ConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class UnmodifiableConfigurationSectionTest extends ConfigurationSectionBaseTest {
    /**
     * The values in the configuration before the test was run.
     */
    private Map<String, Object> values;

    /**
     * The values of the default section in the configuration before the test was run.
     */
    private Map<String, Object> valuesDefault;

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfigurationSection(super.getDefaultConfig());
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
    public void testSetSectionOverwritingExisting() {
        assertThrowsUnmodifiableException(super::testSetSectionOverwritingExisting);
    }

    @Test
    @Override
    public void testSetExistingSectionToNull() {
        assertThrowsUnmodifiableException(super::testSetExistingSectionToNull);
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

    @Test
    @Override
    public void testSetComments() {
        assertThrowsUnmodifiableException(super::testSetComments);
    }

    @Test
    @Override
    public void testSetCommentsOnInvalid() {
        assertThrowsUnmodifiableException(super::testSetCommentsOnInvalid);
    }

    @Test
    @Override
    public void testSetInlineComments() {
        assertThrowsUnmodifiableException(super::testSetInlineComments);
    }

    @Test
    @Override
    public void testSetInlineCommentsOnInvalid() {
        assertThrowsUnmodifiableException(super::testSetInlineCommentsOnInvalid);
    }
}

