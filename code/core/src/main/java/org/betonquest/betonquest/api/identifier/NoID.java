package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;

/**
 * An ID that does not have an actual ID.
 * This is used for runtime-only IDs that are not stored anywhere.
 */
@SuppressWarnings("PMD.ShortClassName")
public class NoID extends DefaultIdentifier {

    /**
     * Constructs a new NoID.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the quest package to use
     * @throws QuestException if the ID cannot be created
     */
    public NoID(final QuestPackageManager packManager, final QuestPackage pack) throws QuestException {
        super(packManager, pack, "NoID");
    }
}
