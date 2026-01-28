package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.identifier.factory.IdentifierRegistry;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderRegistry;

/**
 * Provides the BetonQuest Quest Type Registries.
 * <p>
 * They are used to add new implementations and access them.
 */
public interface QuestTypeRegistries {

    /**
     * Gets the registry for conditions.
     *
     * @return the condition registry
     */
    ConditionRegistry condition();

    /**
     * Gets the registry for actions.
     *
     * @return the action registry
     */
    ActionRegistry action();

    /**
     * Gets the registry for objectives.
     *
     * @return the objective registry
     */
    ObjectiveRegistry objective();

    /**
     * Gets the registry for placeholders.
     *
     * @return the placeholder registry
     */
    PlaceholderRegistry placeholder();

    /**
     * Gets the registry for identifiers.
     *
     * @return the identifier registry
     */
    IdentifierRegistry identifiers();
}
