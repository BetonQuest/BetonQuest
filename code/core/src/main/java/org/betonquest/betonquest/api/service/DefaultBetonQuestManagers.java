package org.betonquest.betonquest.api.service;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestManagers}.
 */
public class DefaultBetonQuestManagers implements BetonQuestManagers {

    /**
     * The {@link ActionManager} supplier to retrieve the {@link ActionManager} instance.
     */
    private final Supplier<ActionManager> actionManager;

    /**
     * The {@link ConditionManager} supplier to retrieve the {@link ConditionManager} instance.
     */
    private final Supplier<ConditionManager> conditionManager;

    /**
     * The {@link ObjectiveManager} supplier to retrieve the {@link ObjectiveManager} instance.
     */
    private final Supplier<ObjectiveManager> objectiveManager;

    /**
     * The {@link ItemManager} supplier to retrieve the {@link ItemManager} instance.
     */
    private final Supplier<ItemManager> itemManager;

    /**
     * The {@link NpcManager} supplier to retrieve the {@link NpcManager} instance.
     */
    private final Supplier<NpcManager> npcManager;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestManagers}.
     *
     * @param actionManager    the action manager supplier
     * @param conditionManager the condition manager supplier
     * @param objectiveManager the objective manager supplier
     * @param itemManager      the item manager supplier
     * @param npcManager       the npc manager supplier
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
