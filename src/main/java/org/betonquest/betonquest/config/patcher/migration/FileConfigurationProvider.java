package org.betonquest.betonquest.config.patcher.migration;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Produces all config files.
 */
public class FileConfigurationProvider {
    /**
     * All configs.
     */
    private final Map<File, YamlConfiguration> allConfigs;

    /**
     * Creates a new file producer.
     *
     * @throws IOException If an I/O error occurs
     */
    public FileConfigurationProvider() throws IOException {
        allConfigs = new LinkedHashMap<>();
        allConfigs.putAll(getAllQuestPackagesConfigs());
        allConfigs.putAll(getAllQuestTemplatesConfigs());
    }

    /**
     * @return All configs
     */
    public Map<File, YamlConfiguration> getAllConfigs() {
        return allConfigs;
    }

    private Map<File, YamlConfiguration> getAllQuestPackagesConfigs() throws IOException {
        final Path path = Paths.get("plugins/BetonQuest/QuestPackages");
        return getAllConfigs(path);
    }

    private Map<File, YamlConfiguration> getAllQuestTemplatesConfigs() throws IOException {
        final Path path = Paths.get("plugins/BetonQuest/QuestTemplates");
        return getAllConfigs(path);
    }

    private Map<File, YamlConfiguration> getAllConfigs(final Path path) throws IOException {
        if (!Files.exists(path)) {
            return Map.of();
        }
        try (Stream<Path> findings = Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().endsWith(".yml"))) {
            final Map<File, YamlConfiguration> configs = new LinkedHashMap<>();
            findings.map(Path::toFile)
                    .forEach(file -> configs.put(file, YamlConfiguration.loadConfiguration(file)));
            return configs;
        }
    }
}
