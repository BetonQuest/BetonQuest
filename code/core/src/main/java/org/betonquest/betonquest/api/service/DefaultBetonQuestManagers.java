package org.betonquest.betonquest.api.service;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestManagers}.
 */
public class DefaultBetonQuestManagers implements BetonQuestManagers {

    /**
     * The {@link ActionManager} supplier.
     */
    private final Supplier<ActionManager> actionManager;

    /**
     * The {@link ConditionManager} supplier.
     */
    private final Supplier<ConditionManager> conditionManager;

    /**
     * The {@link ObjectiveManager} supplier.
     */
    private final Supplier<ObjectiveManager> objectiveManager;

    /**
     * The {@link ItemManager} supplier.
     */
    private final Supplier<ItemManager> itemManager;

    /**
     * The {@link NpcManager} supplier.
     */
    private final Supplier<NpcManager> npcManager;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestManagers}.
     *
     * @param actionManager    the {@link ActionManager} supplier
     * @param conditionManager the {@link ConditionManager} supplier
     * @param objectiveManager the {@link ObjectiveManager} supplier
     * @param itemManager      the {@link ItemManager} supplier
     * @param npcManager       the {@link NpcManager} supplier
     */
    public DefaultBetonQuestManagers(final Supplier<ActionManager> actionManager, final Supplier<ConditionManager> conditionManager,
                                     final Supplier<ObjectiveManager> objectiveManager, final Supplier<ItemManager> itemManager,
                                     final Supplier<NpcManager> npcManager) {
        this.actionManager = actionManager;
        this.conditionManager = conditionManager;
        this.objectiveManager = objectiveManager;
        this.itemManager = itemManager;
        this.npcManager = npcManager;
    }

    @Override
    public ActionManager getActions() {
        return actionManager.get();
    }

    @Override
    public ConditionManager getConditions() {
        return conditionManager.get();
    }

    @Override
    public ObjectiveManager getObjectives() {
        return objectiveManager.get();
    }

    @Override
    public ItemManager getItems() {
        return itemManager.get();
    }

    @Override
    public NpcManager getNpcs() {
        return npcManager.get();
    }
}
