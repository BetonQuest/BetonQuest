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
public class MenuID extends ID {

    private final File file;

    public MenuID(final ConfigPackage pack, final String id) throws ObjectNotFoundException {
        super(pack, id);
        super.rawInstruction = null;
        //find file
        file = new File(super.pack.getFolder(), "menus" + File.separator + super.getBaseID() + ".yml");
        if (!file.exists()) throw new ObjectNotFoundException("Menu '" + getFullID() + "' is not defined");
    }

    /**
     * File where the menus config is located on disk
     */
    public File getFile() {
        return file;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.getBaseID(), super.pack.getName());
    }
}
