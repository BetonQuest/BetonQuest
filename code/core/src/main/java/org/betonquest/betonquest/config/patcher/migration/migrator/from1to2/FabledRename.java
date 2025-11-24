package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Handles the fabled rename migration.
 */
public class FabledRename implements QuestMigration {

    /**
     * Creates a new fabled migrator.
     */
    public FabledRename() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "conditions", "skillapiclass", "fabledclass");
        replaceStartValueInSection(config, "conditions", "skillapilevel", "fabledlevel");
    }
}
