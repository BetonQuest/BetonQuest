package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for {@link MultiConfiguration} as a {@link ConfigurationSection}.
 */
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage"})
public class MultiConfigurationSectionSplitWithConfigurationTest extends MultiConfigurationSectionWithConfigurationTest {
    /**
     * Empty constructor
     */
    public MultiConfigurationSectionSplitWithConfigurationTest() {
        super();
    }

    @Override
    public Configuration getConfig() {
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config1.yml")), "config1.yml");
        configs.put(YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config2.yml")), "config1.yml");
        configs.put(YamlConfiguration.loadConfiguration(new File("src/test/resources/api/bukkit/multi/config3.yml")), "config1.yml");
        try {
            final MultiConfiguration multiConfiguration = new MultiConfiguration(new ArrayList<>(configs.keySet()));
            multiConfiguration.setMultiDefaults(MultiConfigurationSplitTest.getDefault());
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }
}
