package org.betonquest.betonquest.modules.config.patcher.migration;

/**
 * Handels the migration process.
 */
public interface Migrator {

    /**
     * Checks if the migration is needed.
     *
     * @return true if the migration is needed
     */
    boolean needMigration();

    /**
     * Migrates the configs.
     */
    void migrate();
}
