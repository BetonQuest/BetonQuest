package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Add the new {@code dropExcess} flag to the chest-give action.
 */
public class ChestGiveDropFlag implements QuestMigration {

    /**
     * Creates a new chest-give drop flag migration.
     */
    public ChestGiveDropFlag() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        replace(quest.getQuestConfig(), "actions",
                value -> value.startsWith("chestgive "),
                value -> value + " dropExcess");
    }
}
