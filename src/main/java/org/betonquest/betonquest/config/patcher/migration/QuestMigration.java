package org.betonquest.betonquest.config.patcher.migration;

import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Migrates a {@link Quest}.
 */
@FunctionalInterface
public interface QuestMigration {
    /**
     * Migrates the configs.
     *
     * @param quest the Quest to migrate
     * @throws InvalidConfigurationException if an error occurs
     */
    void migrate(Quest quest) throws InvalidConfigurationException;
}
