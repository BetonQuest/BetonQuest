package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.api.config.patcher.PatchTransformerRegisterer;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.modules.config.patcher.DefaultPatchTransformerRegisterer;
import org.betonquest.betonquest.modules.versioning.Version;
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
     * Anonymous {@link PatchTransformerRegisterer} for testing.
     */
    private static final PatchTransformerRegisterer REGISTERER = new DefaultPatchTransformerRegisterer();

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
    void testHasUpdate() throws InvalidConfigurationException {
        final YamlConfiguration configBeforeTest = new YamlConfiguration();
        configBeforeTest.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(logger, config, patch);
        assertTrue(patcher.hasUpdate(), "Patcher did not recognise the possible update.");
        assertEquals(new Version("3.4.5-CONFIG-6"), patcher.getNextConfigVersion(), "Patcher did not return the newest patch version as next config version.");
        assertEquals(configBeforeTest.saveToString(), config.saveToString(), "The patcher must only patch when patcher.patch() is called.");
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
        final Patcher patcher = new Patcher(logger, configFromTheFuture, patch);
        assertFalse(patcher.hasUpdate(), "Patcher recognised patches from outdated versions as possible updates.");
        assertFalse(patcher.updateVersion(), "The Patcher updated the configVersion when it should not.");

        assertEquals(configFromTheFuture.saveToString(), """
                configVersion: 6.2.3-CONFIG-12
                journalLock: false
                """, "The patcher must only patch when patcher.patch() is called.");
    }

    @Test
    @SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
    void testAppliesUpdates() throws InvalidConfigurationException {
        final YamlConfiguration expectedConfig = new YamlConfiguration();
        expectedConfig.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(logger, config, patch);
        assertTrue(patcher.hasUpdate(), "Patcher did not recognise the possible update.");
        assertFalse(patcher.updateVersion(), "The Patcher updated the configVersion when it should not.");

        REGISTERER.registerTransformers(patcher);
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
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testPatchVersionNumberTooShort() throws InvalidConfigurationException {
        final YamlConfiguration invalidConfig = createConfigFromString("""
                "1.0":
                  - type: SET
                    key: journalLock
                    value: true
                """);

        new Patcher(logger, config, invalidConfig);
        verify(logger, times(1)).error(eq("Invalid patch file! A version number is too short or too long."), any(InvalidConfigurationException.class));
        verifyNoMoreInteractions(logger);
    }

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void testPatchMalformed() throws InvalidConfigurationException {
        final YamlConfiguration invalidConfig = createConfigFromString("""
                "1.0": Nonsense
                """);

        new Patcher(logger, config, invalidConfig);
        verify(logger, times(1)).error(eq("Invalid patch file! The patch is malformed."), any(InvalidConfigurationException.class));
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

        new Patcher(logger, config, invalidConfig);
        verify(logger, times(1)).error(eq("Invalid patch file! A version number is too short or too long."), any(InvalidConfigurationException.class));
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
        final Patcher patcher = new Patcher(logger, config, invalidConfig);
        final boolean patchNoError = patcher.patch();
        assertFalse(patchNoError, "Patcher says there were no problems although there were.");
        verify(logger, times(1)).info("Applying patches to update to '3.4.5-CONFIG-6'...");
        verify(logger, times(1)).info("Applying patch of type 'INVALID'...");
        verify(logger, times(1)).warn("There has been an issue while applying the patches for '3.4.5.6': Unknown transformation type 'INVALID' used!");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testEmptyPatchFile() throws InvalidConfigurationException {
        final String patch = "";
        final YamlConfiguration patchConfig = new YamlConfiguration();
        patchConfig.loadFromString(patch);
        final Patcher patcher = new Patcher(logger, config, patchConfig);
        assertFalse(patcher.hasUpdate(), "An empty patch cannot provide updates.");
    }

    @Test
    void testLegacyConfig() throws InvalidConfigurationException {
        final YamlConfiguration emptyConfig = createConfigFromString("");

        final YamlConfiguration patchConfig = createConfigFromString("""
                2.0.0.1:
                - type: SET
                  key: newKey
                  value: newValue
                """);

        final Patcher patcher = new Patcher(logger, emptyConfig, patchConfig);
        REGISTERER.registerTransformers(patcher);
        patcher.patch();
        final YamlConfiguration desiredResult = createConfigFromString("""
                configVersion: 2.0.0-CONFIG-1 #Don't change this! The plugin's automatic config updater handles it.
                newKey: newValue
                """);

        assertEquals(desiredResult.saveToString(), emptyConfig.saveToString(), "The Patcher did not set the configVersion variable on a legacy config.");
        assertEquals("Legacy config", patcher.getCurrentConfigVersion(), "The Patcher did not correctly return a user friendly default version.");
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

        final Patcher patcher = new Patcher(logger, emptyConfig, patchConfig);
        REGISTERER.registerTransformers(patcher);
        patcher.patch();
        assertEquals("100.200.300-CONFIG-400", patcher.getCurrentConfigVersion(), "The Patcher did not return the highest available patch version.");
        assertTrue(patcher.updateVersion(), "The Patcher did not update the configVersion variable.");

        final YamlConfiguration desiredResult = createConfigFromString("""
                configVersion: 100.200.300-CONFIG-400 #Don't change this! The plugin's automatic config updater handles it.
                someKey: someValue
                """);

        assertEquals(desiredResult.saveToString(), emptyConfig.saveToString(), "The Patcher did not set the configVersion variable on a legacy config.");
    }

    private YamlConfiguration createConfigFromString(final String content) throws InvalidConfigurationException {
        final YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(content);
        return config;
    }
}
