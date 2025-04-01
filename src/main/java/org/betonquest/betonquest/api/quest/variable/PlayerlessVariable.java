package org.betonquest.betonquest.api.quest.variable;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * Interface for playerless quest-variables.
 * It represents the playerless variable as described in the BetonQuest user documentation.
 * For the normal variable variant see {@link PlayerVariable}.
 */
@FunctionalInterface
public interface PlayerlessVariable {
    /**
     * Gets the resolved value.
     *
     * @return the value of this variable
     * @throws QuestException when the value could not be retrieved
     */
    String getValue() throws QuestException;
}
