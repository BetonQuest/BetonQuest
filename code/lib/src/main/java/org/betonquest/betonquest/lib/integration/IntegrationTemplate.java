package org.betonquest.betonquest.lib.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.npc.NpcFactory;
import org.betonquest.betonquest.api.quest.npc.NpcReverseIdentifier;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * The default implementation for an integration to be extended offering template methods for more accessibility.
 */
public abstract class IntegrationTemplate implements Integration {

    private final Map<String, BiConsumer<String, BetonQuestApi>> registeredFeatures;

    public IntegrationTemplate() {
        this.registeredFeatures = new HashMap<>();
    }

    protected void registerFeatures(final BetonQuestApi betonQuestApi) {
        registeredFeatures.forEach((name, consumer) -> consumer.accept(name, betonQuestApi));
    }

    protected void registerFeatures(final BetonQuestApi betonQuestApi, final String commonPrefix) {
        registeredFeatures.forEach((name, consumer) -> consumer.accept(commonPrefix + name, betonQuestApi));
    }

    protected void playerAction(final String actionName, final PlayerActionFactory factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().register(name, factory));
    }

    protected void playerlessAction(final String actionName, final PlayerlessActionFactory factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().register(name, factory));
    }

    protected <F extends PlayerActionFactory & PlayerlessActionFactory> void nullableAction(final String actionName, final F factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().registerCombined(name, factory));
    }

    protected void playerCondition(final String conditionName, final PlayerConditionFactory factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().register(name, factory));
    }

    protected void playerlessCondition(final String conditionName, final PlayerlessConditionFactory factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().register(name, factory));
    }

    protected <F extends PlayerConditionFactory & PlayerlessConditionFactory> void nullableCondition(final String conditionName, final F factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().registerCombined(name, factory));
    }

    protected void objective(final String objectiveName, final ObjectiveFactory factory) {
        registeredFeatures.put(objectiveName, (name, api) -> api.objectives().registry().register(name, factory));
    }

    protected void item(final String itemName, final TypeFactory<QuestItemWrapper> factory) {
        registeredFeatures.put(itemName, (name, api) -> api.items().registry().register(name, factory));
    }

    protected void item(final String itemName, final QuestItemSerializer serializer) {
        registeredFeatures.put(itemName, (name, api) -> api.items().registry().registerSerializer(name, serializer));
    }

    protected void item(final String itemName, final TypeFactory<QuestItemWrapper> factory, final QuestItemSerializer serializer) {
        item(itemName, factory);
        item(itemName, serializer);
    }

    protected void npc(final String npcName, final NpcFactory factory) {
        registeredFeatures.put(npcName, (name, api) -> api.npcs().registry().register(name, factory));
    }

    protected void npc(final NpcReverseIdentifier identifier) {
        registeredFeatures.put("", (name, api) -> api.npcs().registry().registerIdentifier(identifier));
    }

    protected void npc(final String npcName, final NpcFactory factory, final NpcReverseIdentifier identifier) {
        npc(npcName, factory);
        npc(identifier);
    }
}
