package org.betonquest.betonquest.compatibility.itemsadder;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.itemsadder.action.IAPlayAnimationActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.action.IASetBlockAtActionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.condition.IABlockConditionFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderItemFactory;
import org.betonquest.betonquest.compatibility.itemsadder.item.ItemsAdderQuestItemSerializer;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockBreakObjectiveFactory;
import org.betonquest.betonquest.compatibility.itemsadder.objective.IABlockPlaceObjectiveFactory;
import org.betonquest.betonquest.lib.integration.IntegrationTemplate;

/**
 * Handles integration with ItemsAdder.
 */
public class ItemsAdderIntegrator extends IntegrationTemplate {

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
        super();
    }

    @Override
    public void enable(final BetonQuestApi api) {

        item("", new ItemsAdderItemFactory(), new ItemsAdderQuestItemSerializer());

        nullableCondition("Block", new IABlockConditionFactory());

        playerAction("Block", new IASetBlockAtActionFactory());
        playerAction("Animation", new IAPlayAnimationActionFactory());

        objective("BlockBreak", new IABlockBreakObjectiveFactory());
        objective("BlockPlace", new IABlockPlaceObjectiveFactory());

        registerFeatures(api, ITEMS_ADDER);
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
