package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;

/**
 * Replaces the legacy `*` with the {@link org.betonquest.betonquest.quest.action.point.PointType#MULTIPLY}.
 */
public class RemoveLegacyPointModification implements QuestMigration {

    /**
     * The empty default constructor.
     */
    public RemoveLegacyPointModification() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        for (final String eventName : List.of("point", "globalpoint", "score")) {
            replace(config, "events",
                    instruction -> instruction.startsWith(eventName + " *"),
                    instruction -> instruction.replace(eventName + " *", eventName) + " action:multiply"
            );
        }
        replace(config, "events",
                instruction -> instruction.startsWith("money *"),
                instruction -> instruction.replace("money *", "money") + " multiply"
        );
    }
}
