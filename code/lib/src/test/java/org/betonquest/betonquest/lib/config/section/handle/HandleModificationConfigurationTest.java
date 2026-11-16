package org.betonquest.betonquest.lib.config.section.handle;

import org.betonquest.betonquest.lib.config.section.handle.util.HandleModificationToConfigurationFixture;
import org.betonquest.betonquest.lib.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link HandleModificationConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.UnitTestShouldIncludeAssert", "PMD.UnitTestAssertionsShouldIncludeMessage", "PMD.JUnitJupiterTestShouldBePackagePrivate"})
public class HandleModificationConfigurationTest extends ConfigurationBaseTest {

    /**
     * The instance of the setter.
     */
    private final HandleModificationToConfigurationFixture setter;

    public HandleModificationConfigurationTest() {
        super(new HandleModificationConfiguration(getDefaultConfig(), new HandleModificationToConfigurationFixture()));
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
    public void add_default_on_root_section() {
        super.add_default_on_root_section();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void add_default_on_root_section_on_existing_config_path() {
        super.add_default_on_root_section_on_existing_config_path();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void add_defaults_as_configuration() {
        super.add_defaults_as_configuration();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void add_defaults_as_configuration_on_existing_config_path() {
        super.add_defaults_as_configuration_on_existing_config_path();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void add_defaults_as_map() {
        super.add_defaults_as_map();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void add_defaults_as_map_on_existing_config_path() {
        super.add_defaults_as_map_on_existing_config_path();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void set_defaults() {
        super.set_defaults();
        setter.getSection().addDefault("default", null);
    }

    @Test
    @Override
    public void set_defaults_on_existing_config_path() {
        super.set_defaults_on_existing_config_path();
        setter.getSection().addDefault("default", null);
    }
}
