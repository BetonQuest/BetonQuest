package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Handles the Ride migration.
 */
public class RideUpdates implements QuestMigration {

    /**
     * Creates a new ride migrator.
     */
    public RideUpdates() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "objectives", "vehicle", "ride");
        replaceStartValueInSection(config, "conditions", "riding", "ride");
    }
}
