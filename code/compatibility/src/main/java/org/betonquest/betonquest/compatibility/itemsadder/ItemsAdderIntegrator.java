package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.itemsadder.action.IAPlayAnimationActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.action.IASetBlockAtActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.condition.IABlockConditionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderItemFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderQuestItemSerializer;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockBreakObjectiveFactory;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockPlaceObjectiveFactory;

/**
 * Handles integration with ItemsAdder.
 */
public class ItemsAdderIntegrator implements Integration {

    /**
     * The minimum required version of ItemsAdder.
     */
    public static final String REQUIRED_VERSION = "4.0.10";

    /**
     * The prefix of implementations.
     */
    private static final String ITEMS_ADDER = "itemsAdder";

    /**
     * The empty default constructor.
     */
    public ItemsAdderIntegrator() {
        // Empty
    }

    @Override
    public void enable(final BetonQuestApi api) {

        final ItemRegistry itemRegistry = api.items().registry();
        itemRegistry.register(ITEMS_ADDER, new ItemsAdderItemFactory());
        itemRegistry.registerSerializer(ITEMS_ADDER, new ItemsAdderQuestItemSerializer());

        final ConditionRegistry condition = api.conditions().registry();
        condition.registerCombined(ITEMS_ADDER + "Block", new IABlockConditionFactory());

        final ActionRegistry action = api.actions().registry();
        action.register(ITEMS_ADDER + "Block", new IASetBlockAtActionFactory());
        action.register(ITEMS_ADDER + "Animation", new IAPlayAnimationActionFactory());

        final ObjectiveRegistry objective = api.objectives().registry();
        objective.register(ITEMS_ADDER + "BlockBreak", new IABlockBreakObjectiveFactory());
        objective.register(ITEMS_ADDER + "BlockPlace", new IABlockPlaceObjectiveFactory());
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
