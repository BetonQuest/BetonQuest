package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the {@link Patcher}.
 */
@ExtendWith(BetonQuestLoggerService.class)
class PatcherTest {

    @Test
    void testHasUpdate() throws InvalidConfigurationException {
        final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.yml"));
        final YamlConfiguration patch = YamlConfiguration.loadConfiguration(new File("src/test/resources/modules.config/config.patch.yml"));

        final Patcher patcher = new Patcher(config, patch);
        assertTrue(patcher.hasUpdate(), "Patcher did not recognise the possible update.");

        final YamlConfiguration configFromTheFuture = new YamlConfiguration();
        configFromTheFuture.loadFromString("configVersion: \"6.2.3-CONFIG-12\"");

        final Patcher anotherPatcher = new Patcher(configFromTheFuture, patch);
        assertFalse(anotherPatcher.hasUpdate(), "Patcher recognised invalid possible updates.");
    }
}
