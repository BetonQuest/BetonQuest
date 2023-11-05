package org.betonquest.betonquest.modules.config.patcher.migration;

import org.betonquest.betonquest.modules.config.patcher.migration.migrators.DoNothingMigrator;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.EventScheduling;
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
import java.util.Map;
import java.util.stream.Stream;

/**
 * Handels the migration process.
 */
public enum Migration {
    /**
     * The migration for the RPGMenu merge.
     */
    RPG_MENU_MERGE(new RPGMenuMerge()),
    /**
     * The migration for the package structure.
     */
    PACKAGE_STRUCTURE(new PackageStructure()),
    /**
     * The migration for the event scheduling.
     */
    EVENT_SCHEDULEING(new EventScheduling(getAllQuestPackagesConfigs())),
    /**
     * The migration for the package section.
     */
    PACKAGE_SELECTION(new PackageSection(getAllQuestPackagesConfigs())),
    /**
     * The migration for the npc_holograms.
     */
    NPC_HOLOGRAMS(new NpcHolograms(getAllQuestConfigs())),
    /**
     * The migration for the effect_lib.
     */
    EFFECT_LIB(new DoNothingMigrator()),
    /**
     * The migration for the MMO updates.
     */
    MMO_UPDATES(new DoNothingMigrator());

    /**
     * The migrator.
     */
    private final Migrator migrator;

    Migration(final Migrator migrator) {
        this.migrator = migrator;
    }

    /**
     * Migrates all configs.
     */
    public static void migrate() {
        boolean needMigration = false;
        for (final Migration migration : values()) {
            if (needMigration || migration.migrator.needMigration()) {
                needMigration = true;
                migration.migrator.migrate();
            }
        }
    }

    private static Map<File, YamlConfiguration> getAllQuestConfigs() {
        final Map<File, YamlConfiguration> configs = getAllQuestPackagesConfigs();
        configs.putAll(getAllQuestTemplatesConfigs());
        return configs;
    }

    private static Map<File, YamlConfiguration> getAllQuestPackagesConfigs() {
        final Path path = Paths.get("plugins/BetonQuest/QuestPackages");
        return getAllConfigs(path);
    }

    private static Map<File, YamlConfiguration> getAllQuestTemplatesConfigs() {
        final Path path = Paths.get("plugins/BetonQuest/QuestTemplates");
        return getAllConfigs(path);
    }

    private static Map<File, YamlConfiguration> getAllConfigs(final Path path) {
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
