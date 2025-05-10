package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigPatcherIT {
    @Test
    void patch_old_config_to_new_config(@TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        final File config = new File("src/main/resources/config.yml");
        final File configPatch = new File("src/main/resources/config.patch.yml");
        final File oldConfig = new File("src/test/resources/config/oldConfigToUpdate.yml");

        final File pluginConfig = new File(tempDir.toFile(), "config.yml");
        Files.copy(oldConfig.toPath(), pluginConfig.toPath());

        final BetonQuestLoggerFactory loggerFactory = mock(BetonQuestLoggerFactory.class);
        when(loggerFactory.create(any(Class.class))).thenReturn(mock(BetonQuestLogger.class));
        when(loggerFactory.create(any(Class.class), anyString())).thenReturn(mock(BetonQuestLogger.class));

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getResource("config.yml")).thenReturn(new FileInputStream(config));
        when(plugin.getResource("config.patch.yml")).thenReturn(new FileInputStream(configPatch));

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));
        configAccessorFactory.createPatching(pluginConfig, plugin, "config.yml");

        final YamlConfiguration yamlPluginConfig = new YamlConfiguration();
        final YamlConfiguration yamlPatchedConfig = new YamlConfiguration();
        yamlPluginConfig.load(new FileReader(config));
        yamlPatchedConfig.load(new FileReader(pluginConfig));

        manualPatchesForExceptions(yamlPatchedConfig);
        assertConfigContains(null, yamlPluginConfig, yamlPatchedConfig);
        assertConfigContains(null, yamlPatchedConfig, yamlPluginConfig);
    }

    private void manualPatchesForExceptions(final YamlConfiguration yamlPatchedConfig) {
        yamlPatchedConfig.set("configVersion", "");
        assertEquals(yamlPatchedConfig.get("default_conversation_IO"), "menu,chest");
        yamlPatchedConfig.set("default_conversation_IO", "menu,tellraw");
    }

    private void assertConfigContains(@Nullable final String parentKey, final ConfigurationSection actual, final ConfigurationSection contains) {
        for (final String key : contains.getKeys(true)) {
            final String actualKey = parentKey == null ? key : parentKey + "." + key;
            if (contains.isConfigurationSection(key)) {
                assertTrue(actual.isConfigurationSection(key), "Key '" + actualKey + "' is missing in the actual config");
                assertConfigContains(actualKey, actual.getConfigurationSection(key), contains.getConfigurationSection(key));
            } else {
                assertTrue(actual.contains(key), "Key '" + actualKey + "' is missing in the actual config");
                assertEquals(contains.get(key), actual.get(key), "Key '" + actualKey + "' has different value in the actual config");
            }
        }
    }
}
