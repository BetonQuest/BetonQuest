package org.betonquest.betonquest.lib.config.section.handle;

import org.betonquest.betonquest.lib.config.section.handle.util.HandleModificationToConfigurationFixture;
import org.betonquest.betonquest.lib.config.util.ConfigurationSectionBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base abstract test class for {@link HandleModificationConfigurationSection}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public abstract class AbstractHandleModificationConfigurationSectionTest extends ConfigurationSectionBaseTest {

    /**
     * The instance of the setter.
     */
    protected HandleModificationToConfigurationFixture setter;

    public AbstractHandleModificationConfigurationSectionTest(final ConfigurationSection config) {
        super(config);
        this.setter = (HandleModificationToConfigurationFixture) ((HandleModificationConfigurationSection) config).handler;
    }

    /**
     * The test is supposed to roll back all changes made to the setter.
     * Therefore, it must be empty after each test.
     */
    @AfterEach
    public void assertSetterIsEmpty() {
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
    public void set() {
        super.set();
        config.set("testSet", null);
    }

    @Test
    @Override
    public void set_on_existing_config_path() {
        super.set_on_existing_config_path();
        config.set("existingSet", null);
    }

    @Test
    @Override
    public void set_section_overwriting_existing() {
        super.set_section_overwriting_existing();
        config.set("childSection", null);
    }

    @Test
    @Override
    public void add_default() {
        super.add_default();
        config.addDefault("default", null);
    }

    @Test
    @Override
    public void add_default_on_existing_config_path() {
        super.add_default_on_existing_config_path();
        config.addDefault("default", null);
    }

    @Test
    @Override
    public void create_section_on_existing_config_path() {
        super.create_section_on_existing_config_path();
        config.set("createdSectionExist", null);
    }

    @Test
    @Override
    public void create_section() {
        super.create_section();
        config.set("createdSection", null);
    }

    @Test
    @Override
    public void create_section_with_values() {
        super.create_section_with_values();
        config.set("createdSectionWithValues", null);
    }

    @Test
    @Override
    public void create_section_with_values_on_existing_config_path() {
        super.create_section_with_values_on_existing_config_path();
        config.set("createdSectionWithValuesExist", null);
    }

    @Test
    @Override
    public void set_comments() {
        super.set_comments();
        config.setComments("existingSet", null);
    }

    @Test
    @Override
    public void set_comments_on_invalid() {
        super.set_comments_on_invalid();
        config.setComments("existingSet_invalid", null);
    }

    @Test
    @Override
    public void set_inline_comments() {
        super.set_inline_comments();
        config.setInlineComments("existingSet", null);
    }

    @Test
    @Override
    public void set_inline_comments_on_invalid() {
        super.set_inline_comments_on_invalid();
        config.setInlineComments("existingSet_invalid", null);
    }
}
