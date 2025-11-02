package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

/**
 * Handles renaming of npc events.
 */
public class NpcEventsRename implements QuestMigration {

    /**
     * Creates a new NPC events migrator.
     */
    public NpcEventsRename() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        replaceStartValueInSection(config, "events", "teleportnpc", "npcteleport");
        replaceStartValueInSection(config, "events", "movenpc", "npcmove");
        replaceStartValueInSection(config, "events", "stopnpc", "npcstop");
    }
}
