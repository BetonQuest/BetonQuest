package org.betonquest.betonquest.api.bukkit.config.custom.multi.fallback;

import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfiguration;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSection;
import org.betonquest.betonquest.api.bukkit.config.custom.fallback.FallbackConfigurationSectionTest;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link FallbackConfigurationSection} class.
 */
@SuppressWarnings("PMD.JUnitAssertionsShouldIncludeMessage")
public class FallbackConfigurationSectionWithMultiFallbackTest extends FallbackConfigurationSectionTest {
    @Override
    public ConfigurationSection getConfig() {
        final Configuration original = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/fallback/original.yml"));
        fallback = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/fallback/fallback.yml"));

        final Configuration defaults = super.getDefaultConfig().getDefaults();
        assertNotNull(defaults);
        original.setDefaults(defaults);

        return new FallbackConfiguration(original, fallback);
    }
}
