package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Map;

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
        return false;
    }

    @Override
    public void migrate() {
    }
}
