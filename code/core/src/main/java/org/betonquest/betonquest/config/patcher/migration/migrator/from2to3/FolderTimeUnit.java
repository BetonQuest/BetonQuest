package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Migrates the folder time unit to the new format "unit:TimeUnit".
 */
public class FolderTimeUnit implements QuestMigration {

    /**
     * Creates a new folder time unit migrator.
     */
    public FolderTimeUnit() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceValueInSection(config, "events", "folder", " ticks", " unit:ticks");
        replaceValueInSection(config, "events", "folder", " seconds", " unit:seconds");
        replaceValueInSection(config, "events", "folder", " minutes", " unit:minutes");
    }
}
