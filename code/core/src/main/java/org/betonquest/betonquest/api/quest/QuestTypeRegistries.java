package org.betonquest.betonquest.api.quest;

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
     * Gets the registry for events.
     *
     * @return the event registry
     */
    ActionRegistry event();

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
}
