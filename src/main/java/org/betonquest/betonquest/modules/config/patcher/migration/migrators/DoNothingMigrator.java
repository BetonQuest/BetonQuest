package org.betonquest.betonquest.modules.config.patcher.migration.migrators;

import org.betonquest.betonquest.modules.config.patcher.migration.Migrator;

public class DoNothingMigrator implements Migrator {

    @Override
    public boolean needMigration() {
        return false;
    }

    @Override
    public void migrate() {
    }
}
