package org.betonquest.betonquest.config.patcher.migration.migrator.from2to3;

import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.config.patcher.migration.QuestMigration;
import org.betonquest.betonquest.config.quest.Quest;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Handles the conversion from string- to component-based text for the menu io.
 */
public class MenuIOComponents implements QuestMigration {

    /**
     * Creates a new menu IO components migrator.
     */
    public MenuIOComponents() {
    }

    @Override
    public void migrate(final Quest quest) throws InvalidConfigurationException {
        final MultiConfiguration config = quest.getQuestConfig();
        final int lineLength = config.getInt("menu_conv_io.line_length");
        if (lineLength != 0) {
            config.set("menu_conv_io.line_length", lineLength * 6);
        }
        config.set("menu_conv_io.option_selected_reset", null);
        config.set("menu_conv_io.option_text_reset", null);
        config.set("menu_conv_io.npc_text_reset", null);
    }
}
