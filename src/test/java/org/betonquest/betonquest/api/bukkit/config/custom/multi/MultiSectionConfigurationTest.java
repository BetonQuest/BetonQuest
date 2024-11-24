package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.betonquest.betonquest.api.bukkit.config.util.ConfigurationBaseTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiSectionConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.UnitTestAssertionsShouldIncludeMessage",
        "PMD.TestClassWithoutTestCases"})
public class MultiSectionConfigurationTest extends ConfigurationBaseTest {

    @Override
    public Configuration getConfig() {
        final Configuration defaultConfig = super.getDefaultConfig();
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(defaultConfig, "config.yml");
        try {
            final MultiConfiguration multiConfiguration = new MultiSectionConfiguration(new ArrayList<>(configs.keySet()));
            final Configuration defaults = defaultConfig.getDefaults();
            assertNotNull(defaults);
            multiConfiguration.setDefaults(defaults);
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
