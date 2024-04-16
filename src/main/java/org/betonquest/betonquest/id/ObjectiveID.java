package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class ObjectiveID extends ID {

    public ObjectiveID(@Nullable final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getString("objectives." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Objective '" + getFullID() + "' is not defined");
        }
    }

}
