package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fixture for working with a {@link Quest}.
 */
@ExtendWith(MockitoExtension.class)
public class QuestFixture {
    protected final YamlConfiguration original = new YamlConfiguration();

    protected final YamlConfiguration expected = new YamlConfiguration();

    /**
     * Temporary quest package.
     */
    @TempDir
    protected Path questDirectory;

    /**
     * Mocked logger.
     */
    @Mock
    protected BetonQuestLogger logger;

    /**
     * Mocked Logger Factory.
     */
    @Mock
    protected BetonQuestLoggerFactory factory;

    protected Quest setupQuest() throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        original.save(packageConfigFile);
        return new Quest(logger, new DefaultConfigAccessorFactory(factory, logger), "test", questDirectory.toFile(), List.of(packageConfigFile));
    }

    protected Quest setupQuest(final String alternativePath)
            throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        new YamlConfiguration().save(packageConfigFile);
        final File alternativeFile = questDirectory.resolve(alternativePath).toFile();
        original.save(alternativeFile);
        return new Quest(logger, new DefaultConfigAccessorFactory(factory, logger), "test", questDirectory.toFile(),
                List.of(packageConfigFile, alternativeFile));
    }

    protected YamlConfiguration loadPackageFile() throws IOException, InvalidConfigurationException {
        return loadFile("package.yml");
    }

    protected YamlConfiguration loadFile(final String path) throws IOException, InvalidConfigurationException {
        final YamlConfiguration yamlConfiguration = new YamlConfiguration();
        yamlConfiguration.load(questDirectory.resolve(path).toFile());
        return yamlConfiguration;
    }

    protected void checkAssertion(final Quest quest, final String fileName) throws IOException, InvalidConfigurationException {
        final ConfigurationSection questConfig = quest.getQuestConfig();
        final ConfigurationSection fileConfig = loadFile(fileName);

        assertConfigContains(null, expected, questConfig);
        assertConfigContains(null, questConfig, expected);
        assertConfigContains(null, expected, fileConfig);
        assertConfigContains(null, fileConfig, expected);
    }

    protected void assertConfigContains(@Nullable final String parentKey, final ConfigurationSection actual, final ConfigurationSection contains) {
        for (final String key : contains.getKeys(true)) {
            final String actualKey = parentKey == null ? key : parentKey + "." + key;
            if (contains.isConfigurationSection(key)) {
                assertTrue(actual.isConfigurationSection(key), "Key '" + actualKey + "' is missing in the actual config");
                assertConfigContains(actualKey, actual.getConfigurationSection(key), contains.getConfigurationSection(key));
            } else {
                assertTrue(actual.contains(key), "Key '" + actualKey + "' is missing in the actual config");
                assertEquals(contains.get(key), actual.get(key), "Key '" + actualKey + "' has different value in the actual config");
                assertEquals(contains.getComments(key), actual.getComments(key), "Key '" + actualKey + "' has different comments in the actual config");
                assertEquals(contains.getInlineComments(key), actual.getInlineComments(key), "Key '" + actualKey + "' has different inline comments in the actual config");
            }
        }
    }
}
