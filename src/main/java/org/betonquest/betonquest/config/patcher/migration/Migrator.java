package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.PackageStructure;
import org.betonquest.betonquest.config.patcher.migration.migrator.from1to2.RPGMenuMerge;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.DeleteMenuConfigYml;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.DeleteMessagesYml;
import org.betonquest.betonquest.config.patcher.migration.migrator.from2to3.MenuConversationSettings;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the migration process of general structure changes.
 */
public class Migrator {
    /**
     * The migrations to use.
     */
    private final List<Migration> migrations;

    /**
     * Creates a new generic migration process.
     *
     * @param loggerFactory the logger factory.
     */
    public Migrator(final BetonQuestLoggerFactory loggerFactory) {
        this.migrations = new LinkedList<>();
        migrations.add(new RPGMenuMerge());
        migrations.add(new PackageStructure());
        migrations.add(new DeleteMessagesYml(loggerFactory.create(DeleteMessagesYml.class)));
        migrations.add(new DeleteMenuConfigYml(loggerFactory.create(DeleteMenuConfigYml.class)));
        migrations.add(new MenuConversationSettings(loggerFactory.create(MenuConversationSettings.class)));
    }

    /**
     * Migrates all generic configs or changes.
     *
     * @throws IOException if an error occurs
     */
    public void migrate() throws IOException {
        for (final Migration migration : migrations) {
            migration.migrate();
        }
    }
}
