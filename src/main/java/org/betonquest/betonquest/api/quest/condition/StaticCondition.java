package org.betonquest.betonquest.api.quest.condition;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Interface for "static" quest-conditions.
 * It represents the "static" condition as described in the BetonQuest user documentation.
 * For the normal condition variant see {@link Condition}.
 */
public interface StaticCondition {
    /**
     * Checks the "static" condition.
     *
     * @return if the condition is fulfilled
     * @throws QuestRuntimeException when the condition check fails
     */
    boolean check() throws QuestRuntimeException;
}
