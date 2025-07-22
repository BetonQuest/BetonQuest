package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.config.patcher.migration.Migration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Handles the menu conversation settings migration with a log warning.
 */
public class MenuConversationSettings implements Migration {
    /**
     * The BetonQuest folder.
     */
    public static final Path BETONQUEST = Paths.get("plugins/BetonQuest");

    /**
     * The BetonQuest quest packages folder.
     */
    public static final Path BETONQUEST_QUEST_PACKAGES = BETONQUEST.resolve("QuestPackages");

    /**
     * The BetonQuest quest templates folder.
     */
    public static final Path BETONQUEST_QUEST_TEMPLATES = BETONQUEST.resolve("QuestTemplates");

    /**
     * The logger.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new instance of the MenuConversationSettings migration.
     *
     * @param log the logger
     */
    public MenuConversationSettings(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public void migrate() throws IOException {
        searchForSettings(BETONQUEST_QUEST_PACKAGES);
        searchForSettings(BETONQUEST_QUEST_TEMPLATES);
    }

    private void searchForSettings(final Path targetPath) throws IOException {
        if (!Files.exists(targetPath)) {
            return;
        }
        try (Stream<Path> paths = Files.find(targetPath, Integer.MAX_VALUE, (p, a) -> p.getFileName().toString().endsWith(".yml"))) {
            paths.forEach(path -> {
                final YamlConfiguration config = YamlConfiguration.loadConfiguration(path.toFile());
                if (config.contains("menu_conv_io")) {
                    log.warn("The menu conversation settings in " + path + " are no longer used, "
                            + "move modified settings to the config.yml file, and delete the settings from the file.");
                }
            });
        }
    }
}
