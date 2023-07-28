package org.betonquest.betonquest.api.bukkit.config.custom.multi;

import org.betonquest.betonquest.api.bukkit.config.util.YamlConfigurationBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the {@link MultiSectionConfiguration}.
 */
@Tag("ConfigurationSection")
@SuppressWarnings({"PMD.JUnit5TestShouldBePackagePrivate", "PMD.JUnitAssertionsShouldIncludeMessage",
        "PMD.TestClassWithoutTestCases"})
public class MultiSectionConfigurationSplitTest extends MultiSectionConfigurationTest {

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
        final Map<ConfigurationSection, String> configs = new HashMap<>();
        configs.put(setupConfig1(), "config1.yml");
        configs.put(setupConfig2(), "config1.yml");
        configs.put(setupConfig3(), "config1.yml");
        try {
            final MultiConfiguration multiConfiguration = new MultiSectionConfiguration(new ArrayList<>(configs.keySet()));
            multiConfiguration.setDefaults(getDefault());
            return multiConfiguration;
        } catch (final KeyConflictException e) {
            fail(e.resolvedMessage(configs), e);
        } catch (final InvalidConfigurationException e) {
            fail(e);
        }
        return null;
    }

    private ConfigurationSection setupConfig1() {
        return new YamlConfigurationBuilder()
                .setupChildSection()
                .setupString()
                .setupDouble()
                .setupStringList()
                .setupDoubleList()
                .setupObject()
                .setupSection()
                .setupOfflinePlayer(UUID.fromString("eba17d33-959d-42a7-a4d9-e9aebef5969e"))
                .build();
    }

    private ConfigurationSection setupConfig2() {
        return new YamlConfigurationBuilder()
                .setupGet()
                .setupInteger()
                .setupLong()
                .setupIntegerList()
                .setupCharacterList()
                .setupVector()
                .setupLocation(world)
                .build();
    }

    private ConfigurationSection setupConfig3() {
        return new YamlConfigurationBuilder()
                .setupExistingSet()
                .setupBoolean()
                .setupList()
                .setupBooleanList()
                .setupMapList()
                .setupColor()
                .setupItem()
                .build();
    }
}
