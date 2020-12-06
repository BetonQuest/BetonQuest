package pl.betoncraft.betonquest.id;

import pl.betoncraft.betonquest.config.ConfigPackage;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;

@SuppressWarnings("PMD.CommentRequired")
public class ItemID extends ID {

    public ItemID(final ConfigPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getString("items." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Item '" + getFullID() + "' is not defined");
        }
    }

}
