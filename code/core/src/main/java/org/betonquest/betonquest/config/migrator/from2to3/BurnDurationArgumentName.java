package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Remove the name of the required argument `duration`.
 */
public class BurnDurationArgumentName implements QuestMigration {

    /**
     * Creates a new instance of {@link BurnDurationArgumentName}.
     */
    public BurnDurationArgumentName() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        replace(quest.getQuestConfig(), "actions", value -> value.startsWith("burn duration:"),
                value -> value.replace("duration:", ""));
        replace(quest.getQuestConfig(), "actions", value -> value.startsWith("burn conditions:"),
                value -> {
                    final String[] split = value.split(" duration:");
                    final int expectedSplitLength = 2;
                    if (split.length == expectedSplitLength) {
                        final String durationValue = split[1];
                        return split[0].replace("burn ", "burn " + durationValue + " ");
                    }
                    return value;
                });
    }
}
