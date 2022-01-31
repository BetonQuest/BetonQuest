package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.api.config.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ID;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Objects;

/**
 * Id of a menu
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuID extends ID {

    private final ConfigurationSection config;

    public MenuID(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        super.rawInstruction = null;
        //find file
        config = super.pack.getConfig().getConfigurationSection("menus." + super.getBaseID());
        if (config == null) {
            throw new ObjectNotFoundException("Menu '" + getFullID() + "' is not defined");
        }
    }

    /**
     * File where the menus config is located on disk
     *
     * @return The menu's config file
     */
    public ConfigurationSection getConfig() {
        return config;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        final MenuID menuID = (MenuID) other;
        return Objects.equals(config, menuID.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getBaseID(), super.pack.getPackagePath());
    }
}
