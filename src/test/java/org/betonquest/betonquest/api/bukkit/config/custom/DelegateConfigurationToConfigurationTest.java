package org.betonquest.betonquest.api.bukkit.config.custom;

import org.betonquest.betonquest.api.bukkit.config.util.AbstractConfigurationTest;
import org.betonquest.betonquest.api.bukkit.config.util.DelegateModificationToConfiguration;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link DelegateConfiguration}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class DelegateConfigurationToConfigurationTest extends AbstractConfigurationTest {
    /**
     * The instance of the setter.
     */
    private DelegateModificationToConfiguration setter;

    /**
     * Empty constructor
     */
    public DelegateConfigurationToConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        setter = new DelegateModificationToConfiguration();
        return new DelegateConfiguration(super.getDefaultConfig(), setter);
    }

    /**
     * The test should not have any entries in the setters do to modifications.
     */
    @AfterEach
    public void afterEach() {
        assertTrue(setter.getSection().getKeys(true).isEmpty());
        final Configuration defaultSection = setter.getSection().getDefaults();
        if (defaultSection != null) {
            assertTrue(defaultSection.getKeys(true).isEmpty());
        }
    }

    @Test
    @Override
    public void testAddDefaultOnRootSection() {
        super.testAddDefaultOnRootSection();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultOnRootSectionOnExistingConfigPath() {
        super.testAddDefaultOnRootSectionOnExistingConfigPath();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfiguration() {
        super.testAddDefaultsAsConfiguration();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultsAsConfigurationOnExistingConfigPath() {
        super.testAddDefaultsAsConfigurationOnExistingConfigPath();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultsAsMap() {
        super.testAddDefaultsAsMap();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultsAsMapOnExistingConfigPath() {
        super.testAddDefaultsAsMapOnExistingConfigPath();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testSetDefaults() {
        super.testSetDefaults();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void testSetDefaultsOnExistingConfigPath() {
        super.testSetDefaultsOnExistingConfigPath();
        setter.getSection().addDefault("default", null);
    }
}
