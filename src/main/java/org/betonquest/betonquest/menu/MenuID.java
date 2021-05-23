package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.config.ConfigPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.ID;

import java.io.File;
import java.util.Objects;

/**
 * Id of a menu
 * <p>
 * Created on 10.02.2018
 *
 * @author Jonas Blocher
 */
@SuppressWarnings("PMD.CommentRequired")
public class MenuID extends ID {

    private final File file;

    public MenuID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        super.rawInstruction = null;
        //find file
        file = new File(super.pack.getFolder(), "menus" + File.separator + super.getBaseID() + ".yml");
        if (!file.exists()) {
            throw new ObjectNotFoundException("Menu '" + getFullID() + "' is not defined");
        }
    }

    /**
     * File where the menus config is located on disk
     *
     * @return The menu's config file
     */
    public File getFile() {
        return file;
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
        return Objects.equals(file, menuID.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.getBaseID(), super.pack.getName());
    }
}
