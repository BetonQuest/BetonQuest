package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Handels the mmo_updates migration.
 */
public class MmoUpdates implements Migrator {

    /**
     * The configs to migrate.
     */
    private final Map<File, YamlConfiguration> configs;

    /**
     * Creates a new mmo_updates migrator.
     *
     * @param configs The configs to migrate.
     */
    public MmoUpdates(final Map<File, YamlConfiguration> configs) {
        this.configs = configs;
    }

    @Override
    public boolean needMigration() {
        return getObjectiveSectionsWithOldMmoInstruction().findAny().isPresent();
    }

    @Override
    public void migrate() {
        getObjectiveSectionsWithOldMmoInstruction().forEach(section -> section.getKeys(false).forEach(key -> {
            final String value = section.getString(key);
            if (value != null) {
                if (value.startsWith("mmocorecastskill")) {
                    section.set(key, "mmoskill " + value.substring("mmocorecastskill".length()) + " trigger:CAST");
                } else if (value.startsWith("mmoitemcastability")) {
                    section.set(key, "mmoskill " + value.substring("mmoitemcastability".length()) + " trigger:RIGHT_CLICK");
                }
            }
        }));

    }

    private Stream<ConfigurationSection> getObjectiveSectionsWithOldMmoInstruction() {
        return configs.values().stream()
                .map(config -> config.getConfigurationSection("objectives"))
                .filter(Objects::nonNull)
                .filter(config -> config.getKeys(false).stream()
                        .map(config::getString)
                        .filter(Objects::nonNull)
                        .anyMatch(value -> value.startsWith("mmocorecastskill") || value.startsWith("mmoitemcastability")));
    }
}
