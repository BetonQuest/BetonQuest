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
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigPatcherIT {
    private static Stream<Arguments> configsToCheck() {
        return Stream.of(
                Arguments.of("config.yml", (Consumer<ConfigurationSection>) section -> {
                    assertEquals(section.get("default_conversation_IO"), "menu,chest");
                    section.set("default_conversation_IO", "menu,tellraw");
                }));
    }

    @ParameterizedTest
    @MethodSource("configsToCheck")
    void patch_old_config_to_new_config(final String mainResource, final Consumer<ConfigurationSection> exceptionsPatcher,
                                        @TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        final String mainResourcePatch = mainResource.replace(".yml", ".patch.yml");

        final File config = new File("src/main/resources/" + mainResource);
        final File configPatch = new File("src/main/resources/" + mainResourcePatch);
        final File oldConfig = new File("src/test/resources/config/" + mainResource.replace(".yml", "Old.yml"));

        final File pluginConfig = new File(tempDir.toFile(), mainResource);
        Files.copy(oldConfig.toPath(), pluginConfig.toPath());

        final BetonQuestLoggerFactory loggerFactory = mock(BetonQuestLoggerFactory.class);
        when(loggerFactory.create(any(Class.class))).thenReturn(mock(BetonQuestLogger.class));
        when(loggerFactory.create(any(Class.class), anyString())).thenReturn(mock(BetonQuestLogger.class));

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getResource(mainResource)).thenReturn(new FileInputStream(config));
        when(plugin.getResource(mainResourcePatch)).thenReturn(new FileInputStream(configPatch));

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));
        configAccessorFactory.createPatching(pluginConfig, plugin, mainResource);

        final YamlConfiguration yamlPluginConfig = new YamlConfiguration();
        final YamlConfiguration yamlPatchedConfig = new YamlConfiguration();
        yamlPluginConfig.load(new FileReader(config));
        yamlPatchedConfig.load(new FileReader(pluginConfig));

        applyException(yamlPatchedConfig, exceptionsPatcher);
        assertConfigContains(null, yamlPluginConfig, yamlPatchedConfig);
        assertConfigContains(null, yamlPatchedConfig, yamlPluginConfig);
    }

    private void applyException(final ConfigurationSection yamlPatchedConfig, final Consumer<ConfigurationSection> exceptionsPatcher) {
        yamlPatchedConfig.set("configVersion", "");
        exceptionsPatcher.accept(yamlPatchedConfig);
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
