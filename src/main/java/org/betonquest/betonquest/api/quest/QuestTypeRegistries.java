package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.event.EventRegistry;
import org.betonquest.betonquest.api.quest.variable.VariableRegistry;

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
    EventRegistry event();

    /**
     * Gets the registry for objectives.
     *
     * @return the objective registry
     */
    FeatureTypeRegistry<Objective> objective();

    /**
     * Gets the registry for variables.
     *
     * @return the variable registry
     */
    VariableRegistry variable();
}
