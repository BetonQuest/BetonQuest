package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.FileProducer;
import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Handels the PackageSection migration.
 */
public class PackageSection implements Migrator {

    /**
     * The config producer.
     */
    private final FileProducer producer;

    /**
     * Creates a new PackageSection migrator.
     *
     * @param producer The config producer
     */
    public PackageSection(final FileProducer producer) {
        this.producer = producer;

    }

    @Override
    public boolean needMigration() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllQuestPackagesConfigs();
        return configs.values().stream().noneMatch(config -> config.contains("enabled"));
    }

    @Override
    public void migrate() throws IOException {
        final Map<File, YamlConfiguration> configs = producer.getAllQuestPackagesConfigs();
        for (final Map.Entry<File, YamlConfiguration> entry : configs.entrySet()) {
            final File file = entry.getKey();
            final YamlConfiguration config = entry.getValue();
            final ConfigurationSection staticSection = config.getConfigurationSection("enabled");
            if (staticSection != null) {
                staticSection.getValues(false).forEach((key, value) -> config.set("packages.enabled", value));
                config.set("enabled", null);
            }
            config.save(file);
        }
    }
}
