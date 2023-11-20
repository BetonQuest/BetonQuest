package org.betonquest.betonquest.modules.config.patcher.migration;

import org.betonquest.betonquest.modules.config.patcher.migration.migrators.DoNothingMigrator;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.NpcHolograms;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.PackageSection;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.PackageStructure;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.RPGMenuMerge;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Handles the migration process.
 */
public class Migration {
    /**
     * The migrators to use.
     */
    private final List<Migrator> migrators;

    /**
     * Creates a new migration process.
     */
    public Migration() {
        this.migrators = new LinkedList<>();

        final Map<File, YamlConfiguration> allCongigs = getAllQuestPackagesConfigs();
        migrators.add(new RPGMenuMerge());
        migrators.add(new PackageStructure());
        migrators.add(new org.betonquest.betonquest.modules.config.patcher.migration.migrators.EventScheduling(allCongigs));
        migrators.add(new PackageSection(allCongigs));
        allCongigs.putAll(getAllQuestTemplatesConfigs());
        migrators.add(new NpcHolograms(allCongigs));
        migrators.add(new DoNothingMigrator()); // EFFECT_LIB
        migrators.add(new DoNothingMigrator()); // MMO_UPDATES
    }

    /**
     * Migrates all configs.
     */
    public void migrate() {
        boolean needMigration = false;
        for (final Migrator migrator : migrators) {
            if (needMigration || migrator.needMigration()) {
                needMigration = true;
                migrator.migrate();
            }
        }
    }

    private Map<File, YamlConfiguration> getAllQuestPackagesConfigs() {
        final Path path = Paths.get("plugins/BetonQuest/QuestPackages");
        return getAllConfigs(path);
    }
    
    private Map<File, YamlConfiguration> getAllQuestTemplatesConfigs() {
        final Path path = Paths.get("plugins/BetonQuest/QuestTemplates");
        return getAllConfigs(path);
    }

    private Map<File, YamlConfiguration> getAllConfigs(final Path path) {
        try (Stream<Path> findings = Files.find(path, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().endsWith(".yml"))) {
            final Map<File, YamlConfiguration> configs = new LinkedHashMap<>();
            findings.map(Path::toFile)
                    .forEach(file -> {
                        try {
                            configs.put(file, YamlConfiguration.loadConfiguration(file));
                        } catch (final Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            return configs;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
