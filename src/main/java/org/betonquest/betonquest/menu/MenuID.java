package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * ID of a menu.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuID extends ID {

    private final ConfigurationSection config;

    @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
    public MenuID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier);
        if (!super.pack.getConfig().isConfigurationSection("menus." + super.getBaseID())) {
            throw new QuestException("Menu '" + getFullID() + "' is not defined");
        }
    }

    /**
     * File where the menus config is located on disk.
     *
     * @return The menu's config file
     */
    public ConfigurationSection getConfig() {
        return config;
    }
}
