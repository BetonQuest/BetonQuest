package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.quest.CoreQuestRegistry;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderRegistry;

/**
 * The BetonQuest registries are responsible for registering custom features.
 * Registries usually implement either the {@link CoreQuestRegistry}, the {@link FeatureTypeRegistry},
 * or the {@link FeatureRegistry} interfaces and offer varying methods for registering custom features with their
 * factories.
 */
public interface BetonQuestRegistries {

    /**
     * Obtains the {@link ActionRegistry} for registering custom actions.
     *
     * @return the action registry
     */
    ActionRegistry getActions();

    /**
     * Obtains the {@link ConditionRegistry} for registering custom conditions.
     *
     * @return the condition registry
     */
    ConditionRegistry getConditions();

    /**
     * Obtains the {@link ObjectiveRegistry} for registering custom {@link Objective}s.
     *
     * @return the objective registry
     */
    ObjectiveRegistry getObjectives();

    /**
     * Obtains the {@link ItemRegistry} for registering custom {@link QuestItem}s and {@link QuestItemSerializer}s.
     *
     * @return the item registry
     */
    ItemRegistry getItems();

    /**
     * Obtains the {@link NpcRegistry} for registering custom NPCs.
     *
     * @return the npc registry
     */
    NpcRegistry getNpcs();

    /**
     * Obtains the {@link PlaceholderRegistry} for registering custom placeholders.
     *
     * @return the placeholder registry
     */
    PlaceholderRegistry getPlaceholders();
}
