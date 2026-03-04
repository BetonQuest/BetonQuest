package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Rename the "global" property of objectives to "auto-once".
 */
public class AutoOnceObjective implements QuestMigration {

    /**
     * Empty default constructor.
     */
    public AutoOnceObjective() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        replace(config, "objectives", value -> true, value -> value
                .replace(" global ", " auto-once "));
        replace(config, "objectives", value -> value.endsWith(" global"), value -> value
                .replace(" global", " auto-once"));
    }
}
