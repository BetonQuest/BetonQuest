package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.lib.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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
    protected BetonQuestLoggerFactory loggerFactory;

    protected Quest setupQuest() throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        original.save(packageConfigFile);
        return new Quest(logger, new DefaultConfigAccessorFactory(loggerFactory, logger), "test", questDirectory.toFile(), List.of(packageConfigFile));
    }

    protected Quest setupQuest(final String alternativePath)
            throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        new YamlConfiguration().save(packageConfigFile);
        final File alternativeFile = questDirectory.resolve(alternativePath).toFile();
        original.save(alternativeFile);
        return new Quest(logger, new DefaultConfigAccessorFactory(loggerFactory, logger), "test", questDirectory.toFile(),
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
        final MultiConfiguration questConfig = quest.getQuestConfig();
        final ConfigurationSection fileConfig = loadFile(fileName);

        assertConfigContains(expected, questConfig, "Expected", false);
        assertConfigContains(questConfig, expected, "Quest", false);
        assertConfigContains(expected, fileConfig, "Expected", true);
        assertConfigContains(fileConfig, expected, "File", true);
    }

    protected void assertConfigContains(final ConfigurationSection actual,
                                        final ConfigurationSection contains, final String actualName, final boolean checkParents) throws InvalidConfigurationException {
        for (final String key : contains.getKeys(true)) {
            if (contains.isConfigurationSection(key)) {
                assertTrue(actual.isConfigurationSection(key), "Key '" + key + "' is missing in the '" + actualName + "' config");
            } else {
                assertTrue(actual.contains(key), "Key '" + key + "' is missing in the '" + actualName + "' config");
                assertEquals(contains.get(key), actual.get(key), "Key '" + key + "' has different value in the '" + actualName + "' config");
                assertEquals(contains.getComments(key), actual.getComments(key), "Key '" + key + "' has different comments in the '" + actualName + "' config");
                assertEquals(contains.getInlineComments(key), actual.getInlineComments(key), "Key '" + key + "' has different inline comments in the '" + actualName + "' config");
                if (checkParents && key.contains(".")) {
                    assertParentComments(key.substring(0, key.lastIndexOf('.')), actual, contains);
                }
            }
        }
    }

    private void assertParentComments(final String key, final ConfigurationSection expected, final ConfigurationSection actual) {
        assertEquals(expected.getComments(key), actual.getComments(key), "Key '" + key + "' has different comments.");
        assertEquals(expected.getInlineComments(key), actual.getInlineComments(key), "Key '" + key + "' has different inline comments.");
        if (key.contains(".")) {
            assertParentComments(key.substring(0, key.lastIndexOf('.')), expected, actual);
        }
    }
}
