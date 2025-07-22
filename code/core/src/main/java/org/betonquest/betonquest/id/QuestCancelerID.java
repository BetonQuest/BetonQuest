package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a quest canceler ID.
 */
public class QuestCancelerID extends ID {

    /**
     * Creates new QuestCancelerID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the quest canceler
     * @throws QuestException if the instruction could not be created or
     *                        when the quest canceler could not be resolved with the given identifier
     */
    public QuestCancelerID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "cancel", "Quest Canceler");
    }
}
