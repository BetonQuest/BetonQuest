package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.AuraSkillsRename;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.EffectLib;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.EventScheduling;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.FabledRename;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.MmoUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.NpcHolograms;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.PackageSection;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.PackageStructure;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.RPGMenuMerge;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.RemoveEntity;
import org.betonquest.betonquest.config.patcher.migration.migrators.from1to2.RideUpdates;
import org.betonquest.betonquest.config.patcher.migration.migrators.from2to3.DeleteMessagesYml;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the migration process.
 */
public class Migrator {
    /**
     * The migrations to use.
     */
    private final List<Migration> migrations;

    /**
     * Creates a new migration process.
     *
     * @param loggerFactory the logger factory.
     * @throws IOException If an I/O error occurs
     */
    public Migrator(final BetonQuestLoggerFactory loggerFactory) throws IOException {
        this.migrations = new LinkedList<>();
        final FileConfigurationProvider provider = new FileConfigurationProvider();

        addMigratorsFrom1to2(provider);
        addMigratorsFrom2to3(loggerFactory, provider);
    }

    private void addMigratorsFrom1to2(final FileConfigurationProvider provider) {
        migrations.add(new RPGMenuMerge());
        migrations.add(new PackageStructure());
        migrations.add(new EventScheduling(provider));
        migrations.add(new PackageSection(provider));
        migrations.add(new NpcHolograms(provider));
        migrations.add(new EffectLib(provider));
        migrations.add(new MmoUpdates(provider));
        migrations.add(new RemoveEntity(provider));
        migrations.add(new RideUpdates(provider));
        migrations.add(new AuraSkillsRename(provider));
        migrations.add(new FabledRename(provider));
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private void addMigratorsFrom2to3(final BetonQuestLoggerFactory loggerFactory, final FileConfigurationProvider provider) {
        migrations.add(new DeleteMessagesYml(loggerFactory.create(DeleteMessagesYml.class)));
    }

    /**
     * Migrates all configs.
     *
     * @throws IOException if an error occurs
     */
    public void migrate() throws IOException {
        for (final Migration migration : migrations) {
            migration.migrate();
        }
    }
}
