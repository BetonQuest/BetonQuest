package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiConfiguration}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationSplitTest extends MultiConfigurationTest {
    /**
     * Empty constructor
     */
    public MultiConfigurationSplitTest() {
        super();
    }

    /**
     * Get the default configuration values as {@link Configuration}.
     *
     * @return the default values
     */
    public static Configuration getDefault() {
        final Configuration defaultSection = new MemoryConfiguration();
        defaultSection.set("default.key", "value");
        return defaultSection;
    }

    @Override
    public Configuration getConfig() {
        final Configuration config1 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config1.yml"));
        final Configuration config2 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config2.yml"));
        final Configuration config3 = YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config3.yml"));
        try {
            final MultiConfiguration multiConfiguration = new MultiConfiguration(config1, config2, config3);
            multiConfiguration.setMultiDefaults(getDefault());
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            final Map<ConfigurationSection, String> configs = new HashMap<>();
            configs.put(config1, "config1.yml");
            configs.put(config2, "config2.yml");
            configs.put(config3, "config3.yml");
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
