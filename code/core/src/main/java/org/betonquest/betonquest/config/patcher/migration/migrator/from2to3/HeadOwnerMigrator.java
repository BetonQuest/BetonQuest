package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

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
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        replaceValueInSection(quest.getQuestConfig(), "items", "simple", "owner:%player%", "owner:");
    }
}
