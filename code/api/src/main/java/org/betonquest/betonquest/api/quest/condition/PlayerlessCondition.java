package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.quest.PrimaryThreadEnforceable;

/**
 * Interface for playerless quest-conditions.
 * It represents the playerless condition as described in the BetonQuest user documentation.
 * For the player condition variant see {@link PlayerCondition}.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface PlayerlessCondition extends PrimaryThreadEnforceable {

    /**
     * Checks the condition.
     *
     * @return if the condition is fulfilled
     * @throws QuestException when the condition check fails
     * @since 3.0.0
     */
    boolean check() throws QuestException;
}
