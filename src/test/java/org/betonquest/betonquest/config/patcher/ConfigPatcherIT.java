package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.config.quest.QuestFixture;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigPatcherIT extends QuestFixture {
    private static Stream<Arguments> configsToCheck() {
        return Stream.of(
                Arguments.of("config.yml", (Consumer<ConfigurationSection>) section -> {
                    assertEquals("menu,chest", section.get("conversation.default_io"),
                            "For old config, conversation.default_io should be menu,chest");
                    section.set("conversation.default_io", "menu,tellraw");
                    assertTrue(section.getBoolean("item.quest.update_legacy_on_join"),
                            "For old config, item.quest.update_legacy_on_join should be true");
                    section.set("item.quest.update_legacy_on_join", false);
                }),
                Arguments.of("lang/de-DE.yml", null),
                Arguments.of("lang/en-US.yml", null),
                Arguments.of("lang/es-ES.yml", null),
                Arguments.of("lang/fr-FR.yml", null),
                Arguments.of("lang/hu-HU.yml", null),
                Arguments.of("lang/it-IT.yml", null),
                Arguments.of("lang/nl-NL.yml", null),
                Arguments.of("lang/pl-PL.yml", null),
                Arguments.of("lang/pt-BR.yml", null),
                Arguments.of("lang/pt-PT.yml", null),
                Arguments.of("lang/ru-RU.yml", null),
                Arguments.of("lang/vi-VN.yml", null),
                Arguments.of("lang/zh-CN.yml", null)
        );
    }

    @ParameterizedTest
    @MethodSource("configsToCheck")
    void patch_old_config_to_new_config(final String mainResource, @Nullable final Consumer<ConfigurationSection> exceptionsPatcher,
                                        @TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        final String mainResourcePatch = mainResource.replace(".yml", ".patch.yml");

        final Path config = Path.of("src/main/resources/" + mainResource);
        final Path configPatch = Path.of("src/main/resources/" + mainResourcePatch);
        final Path configOld = Path.of("src/test/resources/config/" + mainResource.replace(".yml", "-Old.yml"));

        final Path configOldToPatch = tempDir.resolve(mainResource);
        Files.createDirectories(configOldToPatch.getParent());
        Files.copy(configOld, configOldToPatch);

        final BetonQuestLoggerFactory loggerFactory = mock(BetonQuestLoggerFactory.class);
        when(loggerFactory.create(any(Class.class))).thenReturn(mock(BetonQuestLogger.class));
        when(loggerFactory.create(any(Class.class), anyString())).thenReturn(mock(BetonQuestLogger.class));

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getResource(mainResource)).thenReturn(Files.newInputStream(config));
        when(plugin.getResource(mainResourcePatch)).thenReturn(Files.newInputStream(configPatch));

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));
        configAccessorFactory.createPatching(configOldToPatch.toFile(), plugin, mainResource);

        final YamlConfiguration yamlPluginConfig = new YamlConfiguration();
        final YamlConfiguration yamlPatchedConfig = new YamlConfiguration();
        yamlPluginConfig.load(Files.newBufferedReader(config));
        yamlPatchedConfig.load(Files.newBufferedReader(configOldToPatch));

        applyException(yamlPatchedConfig, exceptionsPatcher);
        assertConfigContains(null, yamlPluginConfig, yamlPatchedConfig, "Plugin");
        assertConfigContains(null, yamlPatchedConfig, yamlPluginConfig, "Patched");
    }

    private void applyException(final ConfigurationSection yamlPatchedConfig, @Nullable final Consumer<ConfigurationSection> exceptionsPatcher) {
        yamlPatchedConfig.set("configVersion", "");
        yamlPatchedConfig.setInlineComments("configVersion", null);
        if (exceptionsPatcher != null) {
            exceptionsPatcher.accept(yamlPatchedConfig);
        }
    }
}
