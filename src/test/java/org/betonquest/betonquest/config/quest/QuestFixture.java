package org.betonquest.betonquest.config.quest;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final Set<String> expectedKeys = expected.getKeys(true);
        final Set<String> actualKeys = quest.getQuestConfig().getKeys(true);

        tooManyKeys(expectedKeys, actualKeys, "Missing keys in actual");
        tooManyKeys(actualKeys, expectedKeys, "Too many keys in actual");
        assertEquals(expectedKeys, actualKeys, "Keys do not match in quest");
        assertEquals(expectedKeys, loadFile(fileName).getKeys(true), "Keys do not match in file");
    }

    private void tooManyKeys(final Set<String> one, final Set<String> another, final String message) {
        final Set<String> tooManyActualKeys = new HashSet<>(one);
        tooManyActualKeys.removeAll(another);
        assertEquals(Collections.emptySet(), tooManyActualKeys, message);
    }
}
