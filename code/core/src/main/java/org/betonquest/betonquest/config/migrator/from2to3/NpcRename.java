package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;

import java.util.List;

/**
 * Handles the npc variable (now named Placeholders) rename to quester migration.
 */
public class NpcRename implements QuestMigration {

    /**
     * The old variable (now named Placeholders) value.
     */
    private static final String NPC = "%npc%";

    /**
     * The new variable (now named Placeholders) value.
     */
    private static final String QUESTER = "%quester%";

    /**
     * Creates a new npc to quester variable (now named Placeholders) migrator.
     */
    public NpcRename() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        for (final String key : config.getKeys(true)) {
            if (config.isConfigurationSection(key)) {
                continue;
            }
            replaceValues(key, config);
        }
    }

    private void replaceValues(final String key, final MultiConfiguration config) {
        if (config.isList(key)) {
            final List<String> stringList = config.getStringList(key);
            boolean listChanged = false;
            for (int i = 0; i < stringList.size(); i++) {
                final String value = stringList.get(i);
                if (value.contains(NPC)) {
                    stringList.set(i, value.replaceAll(NPC, QUESTER));
                    listChanged = true;
                }
            }
            if (listChanged) {
                config.set(key, stringList);
            }
        } else {
            final String value = config.getString(key);
            if (value != null && value.contains(NPC)) {
                config.set(key, value.replaceAll(NPC, QUESTER));
            }
        }
    }
}
