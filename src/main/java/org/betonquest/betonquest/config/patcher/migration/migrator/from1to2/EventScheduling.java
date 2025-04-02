package org.betonquest.betonquest.config.patcher.migration.migrator.from1to2;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;

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
    public void migrate(final Quest quest) throws IOException {
        for (final FileConfigAccessor config : quest.getConfigAccessors()) {
            final ConfigurationSection staticSection = config.getConfigurationSection("static");
            if (staticSection == null) {
                continue;
            }
            staticSection.getValues(false).forEach((key, value) -> {
                config.set("schedules." + key + ".type", "realtime-daily");
                config.set("schedules." + key + ".time", key);
                config.set("schedules." + key + ".events", value);
            });
            config.set("static", null);
            config.save();
        }
    }
}
