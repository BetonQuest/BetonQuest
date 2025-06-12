package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.StandardConfigAccessor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link Patcher}.
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class PatcherTest {
    private static DefaultPatchTransformerRegistry registry;

    private FileConfigAccessor config;

    private ConfigAccessor resource;

    private ConfigAccessor patch;

    private ConfigAccessor result;

    @Mock
    private BetonQuestLogger logger;

    @BeforeAll
    static void setUp() {
        registry = new DefaultPatchTransformerRegistry();
    }

    @BeforeEach
    void setupConfig(@TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        config = createConfigAccessorFromResources(tempDir, "config.yml", "src/test/resources/config/config.yml");
        resource = createConfigAccessorFromResources(tempDir, "resource.yml", "src/test/resources/config/resource.yml");
        patch = createConfigAccessorFromResources(tempDir, "config.patch.yml", "src/test/resources/config/config.patch.yml");
        result = createConfigAccessorFromResources(tempDir, "resultingConfig.yml", "src/test/resources/config/resultingConfig.yml");
    }

    @Test
    void applies_updates() throws InvalidConfigurationException {
        resource.set("copied", "resource");
        new Patcher(logger, resource, registry, patch).patch(config);

        verify(logger, times(1)).info("The config file 'config.yml' gets updated from version '1.12.1-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '2.0.0-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '3.4.5-CONFIG-6'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
        assertEquals(getStringFromConfigAccessor(result), getStringFromConfigAccessor(config),
                "Config does not match the expected result.");
    }

    @Test
    void no_set_config_version(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor config = createConfigAccessorFromString(tempDir, "config.yml", "");
        final ConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                "2.0.0.1":
                  - type: SET
                    key: journalLock
                    value: true
                """);
        new Patcher(logger, resource, registry, patch).patch(config);

        verify(logger, times(1)).info("The config file 'config.yml' gets updated from 'legacy' version...");
        verify(logger, times(1)).info("Applying patches to update to '2.0.0-CONFIG-1'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
        assertEquals("""
                configVersion: 2.0.0-CONFIG-1 # Don't change this! The plugin's automatic config updater handles it.
                journalLock: 'true'
                """, getStringFromConfigAccessor(config), "Config does not match the expected result.");
    }

    @Test
    void empty_config_version(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor config = createConfigAccessorFromString(tempDir, "config.yml", """
                configVersion: ""
                """);
        final ConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                "2.0.0.1":
                  - type: SET
                    key: journalLock
                    value: true
                """);
        new Patcher(logger, resource, registry, patch).patch(config);

        verify(logger, times(1)).debug("The config file 'config.yml' gets the latest version '2.0.0-CONFIG-1' set.");
        verifyNoMoreInteractions(logger);
        assertEquals("""
                configVersion: 2.0.0-CONFIG-1 # Don't change this! The plugin's automatic config updater handles it.
                """, getStringFromConfigAccessor(config), "Config does not match the expected result.");
    }

    @Test
    void has_no_update_for_newer_configs(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor config = createConfigAccessorFromString(tempDir, "config.yml", """
                configVersion: 6.2.3-CONFIG-12
                journalLock: false
                """);

        final YamlConfiguration patch = new YamlConfiguration();
        patch.loadFromString("""
                "2.0.0.1":
                  - type: SET
                    key: journalLock
                    value: true
                """);
        new Patcher(logger, resource, registry, patch).patch(config);

        verify(logger, times(1)).debug("The config file 'config.yml' is already up to date.");
        verifyNoMoreInteractions(logger);
        assertEquals("""
                configVersion: 6.2.3-CONFIG-12
                journalLock: false
                """, getStringFromConfigAccessor(config), "Config does not match the expected result.");
    }

    @Test
    void patch_version_number_too_short(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                "1.0":
                  - type: SET
                    key: journalLock
                    value: true
                """);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, resource, registry, patch),
                "The patch file at '1.0' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void patch_malformed(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                "1.0": Nonsense
                """);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, resource, registry, patch),
                "The patch file at '1.0' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void patch_is_nonsense(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                1:
                  - Nonsense
                """);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, resource, registry, patch),
                "The patch file at '1' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void unknown_transformer_type(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", """
                3.4.5.6:
                  - type: INVALID
                    key: journalLock
                    value: megaTrue
                """);
        new Patcher(logger, resource, registry, patch).patch(config);
        verify(logger, times(1)).info("The config file 'config.yml' gets updated from version '1.12.1-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '3.4.5-CONFIG-6'...");
        verify(logger, times(1)).warn("There has been an issue while applying the patches: Unknown transformation type 'INVALID' used!");
        verify(logger, times(1)).warn("The patching progress did not go flawlessly. However, this does not mean your configs are now corrupted. Please check the errors above to see what the patcher did. You might want to adjust your config manually depending on that information.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void empty_patch_file(@TempDir final Path tempDir) throws InvalidConfigurationException, IOException {
        final FileConfigAccessor patch = createConfigAccessorFromString(tempDir, "config.patch.yml", "");
        new Patcher(logger, resource, registry, patch).patch(config);
        verify(logger, times(1)).debug("The config file 'config.yml' has no patches to apply, setting zero version.");
        verifyNoMoreInteractions(logger);
        assertEquals("0.0.0-CONFIG-0", config.getString("configVersion"),
                "Config does not match the expected result.");
    }

    private String getStringFromConfigAccessor(final ConfigAccessor accessor) {
        return ((YamlConfiguration) accessor.getConfig()).saveToString();
    }

    private FileConfigAccessor createConfigAccessorFromResources(final Path tempDir, final String fileName, final String configPath) throws IOException, InvalidConfigurationException {
        final Path path = new File(configPath).toPath();
        return createConfigAccessorFromString(tempDir, fileName, Files.readString(path));
    }

    private FileConfigAccessor createConfigAccessorFromString(final Path tempDir, final String fileName, final String configString) throws IOException, InvalidConfigurationException {
        final Path configFile = tempDir.resolve(fileName);
        Files.write(configFile, configString.getBytes());
        return new StandardConfigAccessor(configFile.toFile(), null, null);
    }
}
