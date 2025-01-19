package org.betonquest.betonquest.config.patcher.transformer;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;

/**
 * Fixture containing the config that is used for testing.
 */
public class TransformersFixture {
    /**
     * The demo config that is used for this test.
     */
    protected static final YamlConfiguration CONFIG = new YamlConfiguration();

    /**
     * The file that contains a demo config for this test.
     */
    protected static final File CONFIG_FILE = new File("src/test/resources/modules.config/config.yml");

    @BeforeEach
    void setupConfig() throws IOException, InvalidConfigurationException {
        CONFIG.load(CONFIG_FILE);
    }
}
