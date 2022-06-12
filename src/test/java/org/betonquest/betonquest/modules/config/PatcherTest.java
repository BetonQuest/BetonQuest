package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.betonquest.betonquest.modules.logger.util.LogValidator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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
    final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.yml"));
    final YamlConfiguration patch = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.patch.yml"));
    final YamlConfiguration invalidPatch = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.invalidPatch.yml"));

    @Test
    void testHasUpdate() throws InvalidConfigurationException {
        final YamlConfiguration configBeforeTest = new YamlConfiguration();
        configBeforeTest.loadFromString(config.saveToString());

        final Patcher patcher = new Patcher(config, patch);
        assertTrue(patcher.hasUpdate(), "Patcher did not recognise the possible update.");

        final YamlConfiguration configFromTheFuture = new YamlConfiguration();
        configFromTheFuture.loadFromString("configVersion: \"6.2.3-CONFIG-12\"");

        final Patcher anotherPatcher = new Patcher(configFromTheFuture, patch);
        assertFalse(anotherPatcher.hasUpdate(), "Patcher recognised invalid possible updates.");

        assertEquals(configBeforeTest.saveToString(), config.saveToString());
    }

    @Test
    void appliesUpdates() throws InvalidConfigurationException {
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

        assertEquals(expectedConfig.saveToString(), config.saveToString());

    }

    @Test
    void generatesErrors(final LogValidator validator) {
        final Patcher patcher = new Patcher(config, invalidPatch);
        validator.assertLogEntry(Level.SEVERE, "Invalid patch file! A version number is too short.");
    }
}
