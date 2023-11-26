package org.betonquest.betonquest.modules.config.patcher.migration;

import org.betonquest.betonquest.modules.config.patcher.migration.migrators.EffectLib;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.EventScheduling;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.MmoUpdates;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.NpcHolograms;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.PackageSection;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.PackageStructure;
import org.betonquest.betonquest.modules.config.patcher.migration.migrators.RPGMenuMerge;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the migration process.
 */
public class Migration {
    /**
     * The migrators to use.
     */
    private final List<Migrator> migrators;

    /**
     * Creates a new migration process.
     *
     * @throws IOException if an error occurs
     */
    public Migration() throws IOException {
        this.migrators = new LinkedList<>();

        migrators.add(new RPGMenuMerge());
        migrators.add(new PackageStructure());
        final FileProducer fileProducer = new FileProducer();
        migrators.add(new EventScheduling(fileProducer));
        migrators.add(new PackageSection(fileProducer));
        migrators.add(new NpcHolograms(fileProducer));
        migrators.add(new EffectLib(fileProducer));
        migrators.add(new MmoUpdates(fileProducer));
    }

    /**
     * Migrates all configs.
     *
     * @throws IOException if an error occurs
     */
    public void migrate() throws IOException {
        boolean needMigration = false;
        for (final Migrator migrator : migrators) {
            if (needMigration || migrator.needMigration()) {
                needMigration = true;
                migrator.migrate();
            }
        }
    }
}
