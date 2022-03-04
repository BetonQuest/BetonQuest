package org.betonquest.betonquest.modules.config;

import org.betonquest.betonquest.modules.logger.util.BetonQuestLoggerService;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test for the {@link Patcher}.
 */
@ExtendWith(BetonQuestLoggerService.class)
public class PatcherTest {

    @Test
    void testHasUpdate() throws IOException, InvalidConfigurationException {
        final Patcher p = setupPatcher(new File("src/test/resources/modules.config/config.yml"), new File("src/test/resources/modules.config/config.patch.yml"));
        assertTrue(p.hasUpdate());

        final Patcher p2 = setupPatcher(new File("src/test/resources/modules.config/configFromTheFuture.yml"), new File("src/test/resources/modules.config/config.patch.yml"));
        assertFalse(p2.hasUpdate());
    }

    private Patcher setupPatcher(final File configFile, final File patchFile) throws IOException, InvalidConfigurationException {
        final YamlConfiguration patch = new YamlConfiguration();
        final YamlConfiguration config = new YamlConfiguration();
        config.load(configFile);
        patch.load(patchFile);

        return new Patcher(config, patch);
    }
}
