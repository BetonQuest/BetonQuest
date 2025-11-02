package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;

import java.util.List;

/**
 * Handles the npc variable rename to quester migration.
 */
public class NpcRename implements QuestMigration {

    /**
     * The old variable value.
     */
    private static final String NPC = "%npc%";

    /**
     * The new variable value.
     */
    private static final String QUESTER = "%quester%";

    /**
     * Creates a new npc to quester variable migrator.
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
