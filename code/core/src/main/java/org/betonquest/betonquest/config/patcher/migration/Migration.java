package org.betonquest.betonquest.config.patcher.migration;

import java.io.IOException;

/**
 * Handles the migration process for generic changes.
 */
@FunctionalInterface
public interface Migration {

    /**
     * Migrates the configs.
     *
     * @throws IOException if an error occurs
     */
    void migrate() throws IOException;
}
