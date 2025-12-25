package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Handles the pick random percentage migration.
 */
public class PickRandomPercentage implements QuestMigration {

    /**
     * Creates a new pick random percentage migration.
     */
    public PickRandomPercentage() {
    }

    @Override
    public void migrate(final Quest quest) {
        final ConfigurationSection section = quest.getQuestConfig().getConfigurationSection("events");
        if (section == null) {
            return;
        }
        for (final String key : section.getKeys(false)) {
            final String value = section.getString(key);
            if (value != null && value.startsWith("pickrandom ")) {
                section.set(key, replacePart(value));
            }
        }
    }

    private String replacePart(final String value) {
        final String[] parts = value.split(" ");
        parts[1] = replaceEvents(parts[1]);
        return StringUtils.join(parts, " ");
    }

    private String replaceEvents(final String eventsList) {
        final String[] events = eventsList.split(",");
        for (int i = 0; i < events.length; i++) {
            events[i] = replacePercentage(events[i]);
        }
        return StringUtils.join(events, ",");
    }

    private String replacePercentage(final String event) {
        final char[] chars = event.toCharArray();
        chars[event.lastIndexOf('%')] = '~';
        return new String(chars);
    }
}
