package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;

/**
 * Changes the %player% to empty string.
 */
public class HeadOwnerMigrator implements QuestMigration {

    /**
     * Create a new HeadOwner Migrator.
     */
    public HeadOwnerMigrator() {
    }

    @Override
    public void migrate(final Quest quest) {
        replaceValueInSection(quest.getQuestConfig(), "items", "simple", "owner:%player%", "owner:");
    }
}
