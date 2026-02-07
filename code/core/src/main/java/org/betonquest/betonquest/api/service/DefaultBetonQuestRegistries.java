package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.item.ItemRegistry;
import org.betonquest.betonquest.api.quest.action.ActionRegistry;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.quest.npc.NpcRegistry;
import org.betonquest.betonquest.api.quest.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.quest.placeholder.PlaceholderRegistry;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestRegistries}.
 */
public class DefaultBetonQuestRegistries implements BetonQuestRegistries {

    /**
     * The {@link ActionRegistry} supplier.
     */
    private final Supplier<ActionRegistry> actionRegistry;

    /**
     * The {@link ConditionRegistry} supplier.
     */
    private final Supplier<ConditionRegistry> conditionRegistry;

    /**
     * The {@link ObjectiveRegistry} supplier.
     */
    private final Supplier<ObjectiveRegistry> objectiveRegistry;

    /**
     * The {@link ItemRegistry} supplier.
     */
    private final Supplier<ItemRegistry> itemRegistry;

    /**
     * The {@link NpcRegistry} supplier.
     */
    private final Supplier<NpcRegistry> npcRegistry;

    /**
     * The {@link PlaceholderRegistry} supplier.
     */
    private final Supplier<PlaceholderRegistry> placeholderRegistry;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestRegistries}.
     *
     * @param actionRegistry      the {@link ActionRegistry} supplier
     * @param conditionRegistry   the {@link ConditionRegistry} supplier
     * @param objectiveRegistry   the {@link ObjectiveRegistry} supplier
     * @param itemRegistry        the {@link ItemRegistry} supplier
     * @param npcRegistry         the {@link NpcRegistry} supplier
     * @param placeholderRegistry the {@link PlaceholderRegistry} supplier
     */
    public DefaultBetonQuestRegistries(final Supplier<ActionRegistry> actionRegistry, final Supplier<ConditionRegistry> conditionRegistry,
                                       final Supplier<ObjectiveRegistry> objectiveRegistry, final Supplier<ItemRegistry> itemRegistry,
                                       final Supplier<NpcRegistry> npcRegistry, final Supplier<PlaceholderRegistry> placeholderRegistry) {
        this.actionRegistry = actionRegistry;
        this.conditionRegistry = conditionRegistry;
        this.objectiveRegistry = objectiveRegistry;
        this.itemRegistry = itemRegistry;
        this.npcRegistry = npcRegistry;
        this.placeholderRegistry = placeholderRegistry;
    }

    @Override
    public ActionRegistry getActions() {
        return actionRegistry.get();
    }

    @Override
    public ConditionRegistry getConditions() {
        return conditionRegistry.get();
    }

    @Override
    public ObjectiveRegistry getObjectives() {
        return objectiveRegistry.get();
    }

    @Override
    public ItemRegistry getItems() {
        return itemRegistry.get();
    }

    @Override
    public NpcRegistry getNpcs() {
        return npcRegistry.get();
    }

    @Override
    public PlaceholderRegistry getPlaceholders() {
        return placeholderRegistry.get();
    }
}
