package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;

/**
 * Represents a quest canceler ID.
 */
public class QuestCancelerID extends ID {

    /**
     * Creates new QuestCancelerID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the quest canceler
     * @throws ObjectNotFoundException when the quest canceler could not be resolved with the given identifier
     */
    public QuestCancelerID(final QuestPackage pack, final String identifier) throws ObjectNotFoundException {
        super(pack, identifier);
        rawInstruction = super.pack.getConfig().getString("cancel." + super.identifier);
        if (rawInstruction == null) {
            throw new ObjectNotFoundException("Quest Canceler '" + getFullID() + "' is not defined");
        }
    }
}
