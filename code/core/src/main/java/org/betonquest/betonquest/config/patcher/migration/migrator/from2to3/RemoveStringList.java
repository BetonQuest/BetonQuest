package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the remove string list migration.
 */
public class RemoveStringList implements QuestMigration {
    /**
     * Creates a new remove string list migration.
     */
    public RemoveStringList() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        migrateSection(config, "npc_holograms", "npcs");
        migrateSection(config, "effectlib", "npcs", "conditions", "locations");
    }

    private void migrateSection(final MultiConfiguration config, final String sectionName, final String... listKey) {
        final ConfigurationSection rootSection = config.getConfigurationSection(sectionName);
        if (rootSection != null) {
            for (final String key : rootSection.getKeys(false)) {
                final ConfigurationSection section = rootSection.getConfigurationSection(key);
                if (section != null) {
                    for (final String listKeyName : listKey) {
                        migrateStringListToString(section, listKeyName);
                    }
                }
            }
        }
    }

    private void migrateStringListToString(final ConfigurationSection section, final String key) {
        if (section.isList(key)) {
            section.set(key, String.join(",", section.getStringList(key)));
        }
    }
}
