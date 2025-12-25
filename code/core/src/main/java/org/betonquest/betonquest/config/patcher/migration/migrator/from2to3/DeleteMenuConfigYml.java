package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.Migration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles the migration of the messages.yml file.
 */
public class DeleteMenuConfigYml implements Migration {

    /**
     * The messages.yml file path.
     */
    public static final Path MENU_CONFIG_YML = Paths.get("plugins/BetonQuest/menuConfig.yml");

    /**
     * The logger.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new instance of the DeleteMessagesYml migration.
     *
     * @param log the logger
     */
    public DeleteMenuConfigYml(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public void migrate() {
        if (Files.exists(MENU_CONFIG_YML)) {
            log.warn("The menuConfig.yml file is no longer used, move modified messages to the lang folder,"
                    + " configure default_close in the config.yml and delete the file.");
        }
    }
}
