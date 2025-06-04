package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Handles the conversion from characters to pixels as width unit in line wrapping for the menu io.
 */
public class MenuIOLineWrapping implements QuestMigration {

    /**
     * Creates a new menu io line wrapping migrator.
     */
    public MenuIOLineWrapping() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        final int lineLength = config.getInt("menu_conv_io.line_length");
        if (lineLength != 0) {
            config.set("menu_conv_io.line_length", lineLength * 6);
        }
    }
}
