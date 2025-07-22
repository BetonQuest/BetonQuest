package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a quest compass ID.
 */
public class CompassID extends ID {

    /**
     * Creates new QuestCompassID instance.
     *
     * @param pack       the package where the identifier was used in
     * @param identifier the identifier of the quest compass
     * @throws QuestException if the instruction could not be created or
     *                        when the quest compass could not be resolved with the given identifier
     */
    public CompassID(@Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(pack, identifier, "compass", "Compass");
    }

    /**
     * Get the full path of the tag to indicate a quest compass should be shown.
     *
     * @return the compass tag
     */
    public String getTag() {
        return getPackage() + ".compass-" + getBaseID();
    }
}
