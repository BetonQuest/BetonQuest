package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Adds the {@code simple } prefix to the {@code items} section to conform the Factory identifiers.
 */
public class AddSimpleTypeToQuestItem implements QuestMigration {

    /**
     * Creates a new simple quest item migration.
     */
    public AddSimpleTypeToQuestItem() {
    }

    @Override
    public void migrate(final Quest quest) {
        final ConfigurationSection section = quest.getQuestConfig().getConfigurationSection("items");
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final String string = section.getString(key);
            section.set(key, "simple " + string);
        }
    }
}
