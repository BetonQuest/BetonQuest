package org.betonquest.betonquest.config.migrator.from2to3;

import org.betonquest.betonquest.lib.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.lib.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

/**
 * Migrates the deprecated {@code run} action to implicit actions.
 * <p>
 * Converts {@code "run ^action1 ^action2"} to a YAML section with numbered sub-keys:
 * <pre>
 * actions:
 *   myRun:
 *     1: "action1"
 *     2: "action2"
 * </pre>
 */
public class RunActionMigrator implements QuestMigration {

    /**
     * The prefix for run actions.
     */
    private static final String RUN_PREFIX = "run ";

    /**
     * Creates a new RunAction migrator.
     */
    public RunActionMigrator() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        expandToSubSection(quest.getQuestConfig(), "actions", RUN_PREFIX, this::splitRunActions);
    }

    @VisibleForTesting
    List<String> splitRunActions(final String runContent) {
        if (runContent.isBlank()) {
            return new ArrayList<>();
        }
        final List<String> actions = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();
        final String[] parts = runContent.split(" ");
        for (final String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    actions.add(builder.toString().trim());
                    builder.setLength(0);
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            actions.add(builder.toString().trim());
        }
        return actions;
    }
}
