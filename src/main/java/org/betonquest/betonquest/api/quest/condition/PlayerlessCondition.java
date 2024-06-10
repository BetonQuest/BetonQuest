package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Interface for playerless quest-conditions.
 * It represents the playerless condition as described in the BetonQuest user documentation.
 * For the player condition variant see {@link PlayerCondition}.
 */
public interface PlayerlessCondition {
    /**
     * Checks the condition.
     *
     * @return if the condition is fulfilled
     * @throws QuestRuntimeException when the condition check fails
     */
    boolean check() throws QuestRuntimeException;
}
