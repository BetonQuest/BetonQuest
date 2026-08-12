package org.betonquest.betonquest.config.migrator.from3to4;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;

/**
 * Adds the required {@code any} argument to {@code deleffect} actions that previously omitted it.
 */
public class DeleteEffectAny implements QuestMigration {

    /**
     * Create the delete effect argument migration.
     */
    public DeleteEffectAny() {
    }

    @Override
    public void migrate(final Quest quest) {
        replace(quest.getQuestConfig(), "actions",
                value -> "deleffect".equals(value) || value.startsWith("deleffect conditions:"),
                value -> value.replaceFirst("deleffect", "deleffect any"));
    }
}
