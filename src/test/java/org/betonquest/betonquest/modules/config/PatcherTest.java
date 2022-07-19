package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the {@link Patcher}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class PatcherTest {

    /**
     * The patch file for this test.
     */
    final YamlConfiguration patch = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.patch.yml"));
    /**
     * The config that will be patched.
     */
    YamlConfiguration config;

    @BeforeEach
    void cleanConfig() {
        config = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.yml"));
    }

    @Test
    void testHasUpdate() throws InvalidConfigurationException {
        final YamlConfiguration configBeforeTest = new YamlConfiguration();
        configBeforeTest.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(config, patch);
        assertTrue(patcher.hasUpdate(), "Patcher did not recognise the possible update.");
        assertEquals(configBeforeTest.saveToString(), config.saveToString(), "The patcher must only patch when patcher.patch() is called.");
    }

    @Test
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
                """)
        ;
        final Patcher anotherPatcher = new Patcher(configFromTheFuture, patch);
        assertFalse(anotherPatcher.hasUpdate(), "Patcher recognised patches from outdated versions as possible updates.");

        assertEquals(configFromTheFuture.saveToString(), """
                configVersion: 6.2.3-CONFIG-12
                journalLock: false
                """, "The patcher must only patch when patcher.patch() is called.");
    }

    @Test
    void testAppliesUpdates() throws InvalidConfigurationException {
        final YamlConfiguration expectedConfig = new YamlConfiguration();
        expectedConfig.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(config, patch);
        patcher.patch();

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

    }


    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testPatchVersionNumberTooShort(final LogValidator validator) throws InvalidConfigurationException {
        final var invalidConfig = createConfigFromString("""
                "1.0":
                  - type: SET
                    key: journalLock
                    value: true
                """);

        new Patcher(config, invalidConfig);
        validator.assertLogEntry(Level.SEVERE, "Invalid patch file! A version number is too short or too long.");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testPatchMalformed(final LogValidator validator) throws InvalidConfigurationException {
        final var invalidConfig = createConfigFromString("""
                "1.0": Nonsense
                """);

        new Patcher(config, invalidConfig);
        validator.assertLogEntry(Level.SEVERE, "Invalid patch file! The patch is malformed.");
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void testPatchIsNonsense(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                1:
                  - Nonsense
                """;
        final var invalidConfig = new YamlConfiguration();
        invalidConfig.loadFromString(patch);

        new Patcher(config, invalidConfig);
        validator.assertLogEntry(Level.SEVERE, "Invalid patch file! A version number is too short or too long.");
    }

    @Test
    void testUnknownTransformerType(final LogValidator validator) throws InvalidConfigurationException {
        final String patch = """
                3.4.5.6:
                  - type: INVALID
                    key: journalLock
                    value: megaTrue
                """;
        final var invalidConfig = new YamlConfiguration();
        invalidConfig.loadFromString(patch);
        final var patcher = new Patcher(config, invalidConfig);
        final boolean patchNoError = patcher.patch();
        assertFalse(patchNoError, "Patcher says there were no problems although there were.");
        validator.assertLogEntry(Level.INFO, "Applying patches to update to '3.4.5.6'...");
        validator.assertLogEntry(Level.WARNING, "There has been an issue while applying the patches for '3.4.5.6': Unknown transformation type 'INVALID' used!");
    }

    @Test
    void testEmptyPatchFile() throws InvalidConfigurationException {
        final String patch = "";
        final var patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);
        final var Patcher = new Patcher(config, patchConfig);
        assertFalse(Patcher.hasUpdate(), "An empty patch cannot provide updates.");
    }


    @Test
    void testLegacyConfig() throws InvalidConfigurationException {
        final var emptyConfig = createConfigFromString("");

        final var patchConfig = createConfigFromString("""
                2.0.0.1:
                - type: SET
                  key: newKey
                  value: newValue
                """);

        final var patcher = new Patcher(emptyConfig, patchConfig);
        patcher.patch();
        final YamlConfiguration desiredResult = createConfigFromString("""
                configVersion: 2.0.0-CONFIG-1 #Don't change this! The plugin's automatic config updater handles it.
                newKey: newValue
                """);

        assertEquals(desiredResult.saveToString(), emptyConfig.saveToString());
    }

    private YamlConfiguration createConfigFromString(final String content) throws InvalidConfigurationException {
        final var config = new YamlConfiguration();
        config.loadFromString(content);
        return config;
    }
}
