package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test for the {@link Patcher}.
 */
@ExtendWith(MockitoExtension.class)
class PatcherTest {
    /**
     * The patch file for this test.
     */
    private final YamlConfiguration patch = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.patch.yml"));

    @Mock
    private BetonQuestLogger logger;

    /**
     * The config that will be patched.
     */
    private YamlConfiguration config;

    @BeforeEach
    void cleanConfig() {
        this.config = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.yml"));
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testHasNoUpdateForNewerConfigs() throws InvalidConfigurationException {
        final YamlConfiguration configFromTheFuture = new YamlConfiguration();
        configFromTheFuture.loadFromString("""
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
        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), patch);
        assertFalse(patcher.patch("config.yml", configFromTheFuture), "The Patcher should not patch a config that is already up to date.");

        assertEquals(configFromTheFuture.saveToString(), """
                configVersion: 6.2.3-CONFIG-12
                journalLock: false
                """, "The patcher must not change the config if it is already up to date.");
        verify(logger, times(1)).debug("The config file 'config.yml' is already up to date.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testAppliesUpdates() throws InvalidConfigurationException {
        final YamlConfiguration expectedConfig = new YamlConfiguration();
        expectedConfig.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), patch);
        assertTrue(patcher.patch("config.yml", config), "The Patcher should patch the config.");

        expectedConfig.set("configVersion", "3.4.5-CONFIG-6");
        expectedConfig.set("journalLock", "megaTrue");
        final List<String> list = expectedConfig.getStringList("section.myList");
        list.set(1, "newEntry");
        list.add("newEntry");
        list.remove("removedEntry");
        expectedConfig.set("section.myList", list);
        expectedConfig.set("section.test", null);
        expectedConfig.set("testNew", "someValue");
        expectedConfig.set("section.testKey", "newTest");
        expectedConfig.set("additionalVal", "42");

        assertEquals(expectedConfig.saveToString(), config.saveToString(), "The patcher must only patch when patcher.patch() is called.");
        verify(logger, times(1)).info("Updating config file 'config.yml' from version '1.12.1-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '2.0.0-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '3.4.5-CONFIG-6'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testPatchVersionNumberTooShort() throws InvalidConfigurationException {
        final YamlConfiguration invalidConfig = createConfigFromString("""
                "1.0":
                  - type: SET
                    key: journalLock
                    value: true
                """);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, new DefaultPatchTransformerRegistry(), invalidConfig), "The patch file at '1.0' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testPatchMalformed() throws InvalidConfigurationException {
        final YamlConfiguration invalidConfig = createConfigFromString("""
                "1.0": Nonsense
                """);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, new DefaultPatchTransformerRegistry(), invalidConfig), "The patch file at '1.0' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testPatchIsNonsense() throws InvalidConfigurationException {
        final String patch = """
                1:
                  - Nonsense
                """;
        final YamlConfiguration invalidConfig = new YamlConfiguration();
        invalidConfig.loadFromString(patch);

        assertThrows(InvalidConfigurationException.class, () -> new Patcher(logger, new DefaultPatchTransformerRegistry(), invalidConfig), "The patch file at '1' is too long or too short.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testUnknownTransformerType() throws InvalidConfigurationException {
        final String patch = """
                3.4.5.6:
                  - type: INVALID
                    key: journalLock
                    value: megaTrue
                """;
        final YamlConfiguration invalidConfig = new YamlConfiguration();
        invalidConfig.loadFromString(patch);
        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), invalidConfig);
        assertTrue(patcher.patch("config.yml", config), "The Patcher should patch the config.");
        verify(logger, times(1)).info("Updating config file 'config.yml' from version '1.12.1-CONFIG-1'...");
        verify(logger, times(1)).info("Applying patches to update to '3.4.5-CONFIG-6'...");
        verify(logger, times(1)).warn("There has been an issue while applying the patches: Unknown transformation type 'INVALID' used!");
        verify(logger, times(1)).warn("The patching progress did not go flawlessly. However, this does not mean your configs are now corrupted. Please check the errors above to see what the patcher did. You might want to adjust your config manually depending on that information.");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testEmptyPatchFile() throws InvalidConfigurationException {
        final String patch = "";
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);
        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), patchConfig);
        assertTrue(patcher.patch("config.yml", config), "The Patcher should patch the config.");
        verify(logger, times(1)).info("Updating config file 'config.yml' from version '1.12.1-CONFIG-1'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
    }

    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    @Test
    void testLegacyConfig() throws InvalidConfigurationException {
        final YamlConfiguration emptyConfig = createConfigFromString("");

        final YamlConfiguration patchConfig = createConfigFromString("""
                2.0.0.1:
                - type: SET
                  key: newKey
                  value: newValue
                """);

        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), patchConfig);
        assertTrue(patcher.patch("config.yml", emptyConfig), "The Patcher should patch the config.");
        final YamlConfiguration desiredResult = createConfigFromString("""
                configVersion: 2.0.0-CONFIG-1 #Don't change this! The plugin's automatic config updater handles it.
                newKey: newValue
                """);

        assertEquals(desiredResult.saveToString(), emptyConfig.saveToString(), "The Patcher did not set the configVersion variable on a legacy config.");
        verify(logger, times(1)).info("Updating config file 'config.yml' from version '0.0.0-CONFIG-0'...");
        verify(logger, times(1)).info("Applying patches to update to '2.0.0-CONFIG-1'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testConfigFromResourceUpdate() throws InvalidConfigurationException {
        final YamlConfiguration emptyConfig = createConfigFromString("""
                        configVersion: ""
                        someKey: someValue
                """);

        final YamlConfiguration patchConfig = createConfigFromString("""
                100.200.300.400:
                - type: SET
                  key: newKey
                  value: newValue
                """);

        final Patcher patcher = new Patcher(logger, new DefaultPatchTransformerRegistry(), patchConfig);
        assertTrue(patcher.patch("config.yml", emptyConfig), "The Patcher should patch the config.");

        final YamlConfiguration desiredResult = createConfigFromString("""
                configVersion: 100.200.300-CONFIG-400 #Don't change this! The plugin's automatic config updater handles it.
                someKey: someValue
                """);

        assertEquals(desiredResult.saveToString(), emptyConfig.saveToString(), "The Patcher did not set the configVersion variable on a legacy config.");
        verify(logger, times(1)).info("Updating config file 'config.yml' from version '100.200.300-CONFIG-400'...");
        verify(logger, times(1)).info("Patching complete!");
        verifyNoMoreInteractions(logger);
    }

    private YamlConfiguration createConfigFromString(final String content) throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(content);
        return config;
    }
}
