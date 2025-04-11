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
import java.util.List;

/**
 * Fixture for working with a {@link Quest}.
 */
@ExtendWith(MockitoExtension.class)
public class QuestFixture {
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

    protected Quest setupQuest(final YamlConfiguration original) throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        original.save(packageConfigFile);
        return new Quest(logger, new DefaultConfigAccessorFactory(factory, logger), "test", questDirectory.toFile(), List.of(packageConfigFile));
    }

    protected Quest setupQuest(final String alternativePath, final YamlConfiguration alternative)
            throws IOException, InvalidConfigurationException {
        final File packageConfigFile = questDirectory.resolve("package.yml").toFile();
        new YamlConfiguration().save(packageConfigFile);
        final File alternativeFile = questDirectory.resolve(alternativePath).toFile();
        alternative.save(alternativeFile);
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
}
