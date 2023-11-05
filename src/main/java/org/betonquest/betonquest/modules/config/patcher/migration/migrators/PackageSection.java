package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

/**
 * Handels the PackageSection migration.
 */
public class PackageSection implements Migrator {

    /**
     * The configs to migrate.
     */
    private final Map<File, YamlConfiguration> configs;

    /**
     * Creates a new PackageSection migrator.
     *
     * @param configs The configs to migrate.
     */
    public PackageSection(final Map<File, YamlConfiguration> configs) {
        this.configs = configs;
    }

    @Override
    public boolean needMigration() {
        return configs.values().stream().noneMatch(config -> config.contains("enabled"));
    }

    @Override
    public void migrate() {
        configs.forEach((file, config) -> {
            final ConfigurationSection staticSection = config.getConfigurationSection("enabled");
            if (staticSection != null) {
                staticSection.getValues(false).forEach((key, value) -> {
                    config.set("packages.enabled", value);
                });
                config.set("enabled", null);
            }
            try {
                config.save(file);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
