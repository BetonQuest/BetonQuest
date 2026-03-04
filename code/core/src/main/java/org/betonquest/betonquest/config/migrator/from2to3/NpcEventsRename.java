package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;

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
