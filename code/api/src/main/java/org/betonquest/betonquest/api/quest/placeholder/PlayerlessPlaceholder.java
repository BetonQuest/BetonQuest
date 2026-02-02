package org.betonquest.betonquest.api.quest.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for playerless quest-placeholders.
 * It represents the playerless placeholder as described in the BetonQuest user documentation.
 * For the normal placeholder variant see {@link PlayerPlaceholder}.
 */
@FunctionalInterface
public interface PlayerlessPlaceholder extends PrimaryThreadEnforceable {

    /**
     * Gets the resolved value.
     *
     * @return the value of this placeholder
     * @throws QuestException when the value could not be retrieved
     */
    String getValue() throws QuestException;
}
