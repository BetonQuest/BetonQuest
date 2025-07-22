package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the MmoUpdates migration.
 */
public class MmoUpdates implements QuestMigration {

    /**
     * Creates a new mmo_updates migrator.
     */
    public MmoUpdates() {
    }

    @Override
    public void migrate(final Quest quest) {
        final MultiConfiguration config = quest.getQuestConfig();
        final ConfigurationSection objectives = config.getConfigurationSection("objectives");
        if (objectives == null) {
            return;
        }
        for (final String key : objectives.getKeys(false)) {
            final String value = objectives.getString(key);
            if (value == null) {
                continue;
            }
            if (value.startsWith("mmocorecastskill ")) {
                objectives.set(key, "mmoskill " + value.substring("mmocorecastskill ".length()) + " trigger:CAST");
            } else if (value.startsWith("mmoitemcastability ")) {
                objectives.set(key, "mmoskill " + value.substring("mmoitemcastability ".length()) + " trigger:RIGHT_CLICK");
            }
        }
    }
}
