package org.betonquest.betonquest.modules.config.patcher.migration;

import java.io.IOException;

/**
 * Handels the migration process.
 */
public interface Migrator {

    /**
     * Checks if the migration is needed.
     *
     * @return true if the migration is needed
     */
    boolean needMigration() throws IOException;

    /**
     * Migrates the configs.
     *
     * @throws IOException if an error occurs
     */
    void migrate() throws IOException;
}
