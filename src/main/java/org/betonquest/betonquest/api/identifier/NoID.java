package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;

/**
 * An ID that does not have an actual ID.
 * This is used for runtime-only IDs that are not stored anywhere.
 */
@SuppressWarnings("PMD.ShortClassName")
public class NoID extends Identifier {

    /**
     * Constructs a new NoID.
     *
     * @param pack the quest package to use
     * @throws QuestException if the ID cannot be created
     */
    public NoID(final QuestPackage pack) throws QuestException {
        super(pack, "NoID");
    }
}
