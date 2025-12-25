package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.Migration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles the migration of the messages.yml file.
 */
public class DeleteMessagesYml implements Migration {

    /**
     * The messages.yml file path.
     */
    public static final Path MESSAGES_YML = Paths.get("plugins/BetonQuest/messages.yml");

    /**
     * The logger.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new instance of the DeleteMessagesYml migration.
     *
     * @param log the logger
     */
    public DeleteMessagesYml(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public void migrate() {
        if (Files.exists(MESSAGES_YML)) {
            log.warn("The messages.yml file is no longer used, move modified messages to the lang folder and delete the file.");
        }
    }
}
