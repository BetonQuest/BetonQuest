package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.handle.util.HandleModificationToConfiguration;
import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link HandleModificationConfiguration}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.AvoidDuplicateLiterals", "PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
public class HandleModificationConfigurationTest extends ConfigurationBaseTest {

    /**
     * The instance of the setter.
     */
    private HandleModificationToConfiguration setter;

    /**
     * Empty constructor
     */
    public HandleModificationConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        setter = new HandleModificationToConfiguration();
        return new HandleModificationConfiguration(super.getDefaultConfig(), setter);
    }

    /**
     * The test is supposed to roll back all changes made to the setter.
     * Therefore, it must be empty after each test.
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
