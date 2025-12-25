package org.betonquest.betonquest.config.patcher.transformer;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.config.StandardConfigAccessor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fixture containing the config that is used for testing.
 */
public class TransformersFixture {

    /**
     * The demo config that is used for this test.
     */
    protected FileConfigAccessor config;

    @BeforeEach
    void setupConfig(@TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        config = createConfigAccessorFromResources(tempDir, "config.yml", "src/test/resources/config/config.yml");
    }

    /**
     * Get the string from a config accessor.
     *
     * @param accessor the accessor
     * @return the string
     */
    protected String getStringFromConfigAccessor(final ConfigAccessor accessor) {
        return ((YamlConfiguration) accessor.getConfig()).saveToString();
    }

    /**
     * Create a config accessor from a resource.
     *
     * @param tempDir    the temp directory
     * @param fileName   the file name
     * @param configPath the config path
     * @return the config accessor
     * @throws IOException                   if an I/O error occurs
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    protected FileConfigAccessor createConfigAccessorFromResources(final Path tempDir, final String fileName, final String configPath) throws IOException, InvalidConfigurationException {
        final Path path = new File(configPath).toPath();
        return createConfigAccessorFromString(tempDir, fileName, Files.readString(path));
    }

    /**
     * Create a config accessor from a string.
     *
     * @param tempDir      the temp directory
     * @param fileName     the file name
     * @param configString the config string
     * @return the config accessor
     * @throws IOException                   if an I/O error occurs
     * @throws InvalidConfigurationException if the configuration is invalid
     */
    protected FileConfigAccessor createConfigAccessorFromString(final Path tempDir, final String fileName, final String configString) throws IOException, InvalidConfigurationException {
        final Path configFile = tempDir.resolve(fileName);
        Files.writeString(configFile, configString, StandardCharsets.UTF_8);
        return new StandardConfigAccessor(configFile.toFile(), null, null);
    }
}
