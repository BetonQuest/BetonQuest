package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;

/**
 * An ID that does not have an actual ID.
 * This is used for runtime-only IDs that are not stored anywhere.
 */
@SuppressWarnings("PMD.ShortClassName")
public class NoID extends DefaultIdentifier {

    /**
     * Constructs a new NoID.
     *
     * @param pack the quest package to use
     */
    public NoID(final QuestPackage pack) {
        super(pack, "NoID");
    }
}
