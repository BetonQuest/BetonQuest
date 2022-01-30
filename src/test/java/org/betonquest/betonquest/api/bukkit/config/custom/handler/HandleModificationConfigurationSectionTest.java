package org.betonquest.betonquest.api.bukkit.config.custom.handler;

import org.betonquest.betonquest.api.bukkit.config.custom.handler.util.HandleModificationToConfiguration;
import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link HandleModificationConfigurationSection}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class HandleModificationConfigurationSectionTest extends ConfigurationSectionBaseTest {
    /**
     * The instance of the setter.
     */
    protected HandleModificationToConfiguration setter;

    /**
     * Empty constructor
     */
    public HandleModificationConfigurationSectionTest() {
        super();
    }

    @Override
    public ConfigurationSection getConfig() {
        setter = new HandleModificationToConfiguration();
        return new HandleModificationConfigurationSection(super.getDefaultConfig(), setter);
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
    public void testSet() {
        super.testSet();
        config.set("testSet", null);
    }

    @Test
    @Override
    public void testSetOnExistingConfigPath() {
        super.testSetOnExistingConfigPath();
        config.set("existingSet", null);
    }

    @Test
    @Override
    public void testAddDefault() {
        super.testAddDefault();
        config.addDefault("default", null);
    }

    @Test
    @Override
    public void testAddDefaultOnExistingConfigPath() {
        super.testAddDefaultOnExistingConfigPath();
        config.addDefault("default", null);
    }

    @Test
    @Override
    public void testCreateSectionOnExistingConfigPath() {
        super.testCreateSectionOnExistingConfigPath();
        config.set("createdSectionExist", null);
    }

    @Test
    @Override
    public void testCreateSection() {
        super.testCreateSection();
        config.set("createdSection", null);
    }

    @Test
    @Override
    public void testCreateSectionWithValues() {
        super.testCreateSectionWithValues();
        config.set("createdSectionWithValues", null);
    }

    @Test
    @Override
    public void testCreateSectionWithValuesOnExistingConfigPath() {
        super.testCreateSectionWithValuesOnExistingConfigPath();
        config.set("createdSectionWithValuesExist", null);
    }
}
