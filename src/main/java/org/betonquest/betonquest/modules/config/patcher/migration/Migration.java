package org.betonquest.betonquest.modules.config.patcher.migration;

import java.io.IOException;

/**
 * Handles the migration process.
 */
public interface Migration {
    /**
     * Migrates the configs.
     *
     * @throws IOException if an error occurs
     */
    void migrate() throws IOException;
}
