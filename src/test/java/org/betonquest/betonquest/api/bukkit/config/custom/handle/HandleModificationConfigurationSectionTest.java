package org.betonquest.betonquest.api.bukkit.config.custom.handle;

import org.betonquest.betonquest.api.bukkit.config.custom.handle.util.HandleModificationToConfiguration;
import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link HandleModificationConfigurationSection}.
 */
@SuppressWarnings({"PMD.JUnitTestsShouldIncludeAssert", "PMD.JUnitAssertionsShouldIncludeMessage", "PMD.JUnit5TestShouldBePackagePrivate"})
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
        assertTrue(setter.getComments().isEmpty());
        assertTrue(setter.getInlineComments().isEmpty());
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

    @Test
    @Override
    public void testSetComments() {
        super.testSetComments();
        config.setComments("existingSet", null);
    }

    @Test
    @Override
    public void testSetCommentsOnInvalid() {
        super.testSetCommentsOnInvalid();
        config.setComments("existingSet_invalid", null);
    }

    @Test
    @Override
    public void testSetInlineComments() {
        super.testSetInlineComments();
        config.setInlineComments("existingSet", null);
    }

    @Test
    @Override
    public void testSetInlineCommentsOnInvalid() {
        super.testSetInlineCommentsOnInvalid();
        config.setInlineComments("existingSet_invalid", null);
    }
}
