package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Handles the remove entity migration.
 */
public class RemoveEntity implements QuestMigration {

    /**
     * Creates a new mmo_updates migrator.
     */
    public RemoveEntity() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "events", "clear", "removeentity");
        replaceStartValueInSection(config, "events", "killmob", "removeentity");
    }
}
