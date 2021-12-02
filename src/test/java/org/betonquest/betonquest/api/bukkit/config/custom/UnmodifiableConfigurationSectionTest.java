package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationSectionTest;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
public class UnmodifiableConfigurationSectionTest extends AbstractConfigurationSectionTest {
    public UnmodifiableConfigurationSectionTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        return new UnmodifiableConfigurationSection(super.getConfig());
    }

    private void assertThrowsException(final Executable executable) {
        final Exception exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals("This config is unmodifiable", exception.getMessage());
    }

    @Test
    @Override
    public void testCreateSectionWithValuesOnExistingConfigPath() {
        assertThrowsException(super::testCreateSectionWithValuesOnExistingConfigPath);
    }

    @Test
    @Override
    public void testAddDefaultOnChildSection() {
        assertThrowsException(super::testAddDefaultOnChildSection);
    }

    @Test
    @Override
    public void testSet() {
        assertThrowsException(super::testSet);
    }

    @Test
    @Override
    public void testSetOnExistingConfigPath() {
        assertThrowsException(super::testSetOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSectionOnExistingConfigPath() {
        assertThrowsException(super::testCreateSectionOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSection() {
        assertThrowsException(super::testCreateSection);
    }

    @Test
    @Override
    public void testAddDefaultOnChildSectionOnExistingConfigPath() {
        assertThrowsException(super::testAddDefaultOnChildSectionOnExistingConfigPath);
    }

    @Test
    @Override
    public void testCreateSectionWithValues() {
        assertThrowsException(super::testCreateSectionWithValues);
    }
}
