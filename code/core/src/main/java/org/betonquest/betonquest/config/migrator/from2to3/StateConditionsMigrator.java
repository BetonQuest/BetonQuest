package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Migrates "fly", "sneak" and "burning" conditions to "state" conditions.
 */
public class StateConditionsMigrator implements QuestMigration {

    /**
     * Empty default constructor.
     */
    public StateConditionsMigrator() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        migrateCondition(quest, "fly", "state GLIDING");
        migrateCondition(quest, "sneak", "state SNEAKING");
        migrateCondition(quest, "burning", "state FIRE");
    }

    private void migrateCondition(final Quest quest, final String oldType, final String newInstruction) {
        replace(quest.getQuestConfig(), "conditions",
                value -> value.startsWith(oldType),
                value -> newInstruction + value.substring(oldType.length()));
    }
}
