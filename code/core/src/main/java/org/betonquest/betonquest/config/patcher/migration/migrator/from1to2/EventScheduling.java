package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Handles the EventScheduling migration.
 */
public class EventScheduling implements QuestMigration {

    /**
     * Creates a new EventScheduling migrator.
     */
    public EventScheduling() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        final ConfigurationSection staticSection = config.getConfigurationSection("static");
        final ConfigurationSection source = config.getSourceConfigurationSection("static");
        if (staticSection == null || source == null) {
            return;
        }
        staticSection.getValues(false).forEach((key, value) -> {
            config.set("schedules." + key + ".type", "realtime-daily");
            config.set("schedules." + key + ".time", key);
            config.set("schedules." + key + ".events", value);
        });
        config.set("static", null);
        config.associateWith(source);
    }
}
