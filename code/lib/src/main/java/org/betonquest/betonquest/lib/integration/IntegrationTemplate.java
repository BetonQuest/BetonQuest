package org.betonquest.betonquest.lib.integration;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;

import java.util.function.BiConsumer;

/**
 * The default implementation for an integration to be extended, offering template methods for simplified
 * feature registration.
 * <p>
 * This class provides convenient methods to register various BetonQuest features such as actions, conditions,
 * objectives, items, and NPCs. Instead of manually interacting with the {@link BetonQuestApi}, subclasses can
 * use the provided template methods to register features, which will then be bulk-registered when
 * {@link #registerFeatures(BetonQuestApi)} or {@link #registerFeatures(BetonQuestApi, String)} is called.
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * public class MyIntegration extends IntegrationTemplate {
 *     public void enable(BetonQuestApi api) {
 *         // Registers a PlayerAction "myAction" with the factory MyActionFactory
 *         // Instead of: api.actions().registry().register("myplugin_myAction", new MyActionFactory());
 *         playerAction("myAction", new MyActionFactory());
 *         // Registers a NullableCondition "myCondition" with the factory MyConditionFactory
 *         // Instead of: api.conditions().registry().registerCombined("myplugin_myCondition", new MyConditionFactory());
 *         nullableCondition("myCondition", new MyConditionFactory());
 *         // Enables all registered features using the BetonQuestApi
 *         // The prefix "myplugin_" will be added to all feature names
 *         // so that they are more unique across all integrations
 *         registerFeatures(api, "myplugin_");
 *     }
 * }
 * }</pre>
 *
 * @see Integration
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class IntegrationTemplate implements Integration {

    /**
     * Holds all registered features for their registration.
     */
    private final Multimap<String, BiConsumer<String, BetonQuestApi>> registeredFeatures;

    /**
     * Creates a new IntegrationTemplate.
     */
    public IntegrationTemplate() {
        this.registeredFeatures = HashMultimap.create();
    }

    /**
     * Registers all features that have been previously registered via the template methods.
     * <p>
     * This method processes all features registered through methods such as {@link #playerAction},
     * {@link #playerCondition}, {@link #objective}, etc., and registers them with the {@link BetonQuestApi}
     * using their original names without any prefix.
     * <p>
     * <b>Note:</b> If you need to add a common prefix to all feature names to ensure uniqueness across
     * integrations, use {@link #registerFeatures(BetonQuestApi, String)} instead.
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance to register the features to
     * @see #registerFeatures(BetonQuestApi, String)
     */
    protected void registerFeatures(final BetonQuestApi betonQuestApi) {
        registeredFeatures.forEach((name, consumer) -> consumer.accept(name, betonQuestApi));
    }

    /**
     * Registers all features that have been registered with a common prefix added to the name of all features.
     * <p>
     * This method processes all features previously registered via the template methods
     * (such as {@link #playerAction}, {@link #playerCondition}, {@link #objective}, etc.) and
     * registers them with the {@link BetonQuestApi}. The provided {@code commonPrefix} is prepended
     * to each feature name before registration, which helps ensure unique identifiers across different
     * integrations and prevents naming conflicts.
     * <p>
     * <b>Example:</b>
     * <pre>{@code
     * // Features registered as:
     * playerAction("teleport", new TeleportActionFactory());
     * objective("collect", new CollectObjectiveFactory());
     *
     * // Will be registered with BetonQuest as:
     * registerFeatures(api, "myplugin_");
     * // Results in: "myplugin_teleport" and "myplugin_collect"
     * }</pre>
     *
     * @param betonQuestApi the {@link BetonQuestApi} instance to register the features to
     * @param commonPrefix  the prefix to prepend to the name of all features during registration;
     *                      typically follows the pattern "pluginname_" to ensure uniqueness
     * @see #registerFeatures(BetonQuestApi)
     */
    protected void registerFeatures(final BetonQuestApi betonQuestApi, final String commonPrefix) {
        registeredFeatures.forEach((name, consumer) -> consumer.accept(commonPrefix + name, betonQuestApi));
    }

    /**
     * Registers a player action with the given name and factory.
     *
     * @param actionName the name of the action that will identify it in the script
     * @param factory    the factory to create instances of the player action
     */
    protected void playerAction(final String actionName, final PlayerActionFactory factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().register(name, factory));
    }

    /**
     * Registers a playerless action with the given name and factory.
     *
     * @param actionName the name of the action that will identify it in the script
     * @param factory    the factory to create instances of the playerless action
     */
    protected void playerlessAction(final String actionName, final PlayerlessActionFactory factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().register(name, factory));
    }

    /**
     * Registers a type with a given name and a factory capable of creating both player and playerless actions.
     *
     * @param actionName the name of the action type that will identify it in the script
     * @param factory    the factory to create both player and playerless variants of the action type
     * @param <F>        the type of the factory that extends both
     *                   {@link PlayerActionFactory} and {@link PlayerlessActionFactory}
     */
    protected <F extends PlayerActionFactory & PlayerlessActionFactory> void nullableAction(final String actionName, final F factory) {
        registeredFeatures.put(actionName, (name, api) -> api.actions().registry().registerCombined(name, factory));
    }

    /**
     * Registers a player condition with the given name and factory.
     *
     * @param conditionName the name of the condition that will identify it in the script
     * @param factory       the factory used to create instances of the condition
     */
    protected void playerCondition(final String conditionName, final PlayerConditionFactory factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().register(name, factory));
    }

    /**
     * Registers a playerless condition with the given name and factory.
     *
     * @param conditionName the name of the playerless condition that will identify it in the script
     * @param factory       the factory to create instances of the playerless condition
     */
    protected void playerlessCondition(final String conditionName, final PlayerlessConditionFactory factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().register(name, factory));
    }

    /**
     * Registers a nullable condition under the given name and factory capable of creating
     * both player and playerless variants of the condition.
     *
     * @param conditionName the name of the condition type that will identify it in the script
     * @param factory       the factory to create both player and playerless variants of the condition type
     * @param <F>           the type of the factory that extends both
     *                      {@link PlayerConditionFactory} and {@link PlayerlessConditionFactory}
     */
    protected <F extends PlayerConditionFactory & PlayerlessConditionFactory> void nullableCondition(final String conditionName, final F factory) {
        registeredFeatures.put(conditionName, (name, api) -> api.conditions().registry().registerCombined(name, factory));
    }

    /**
     * Registers an objective with the given name and factory.
     *
     * @param objectiveName the name of the objective that will identify it in the script
     * @param factory       the factory to create instances of the objective
     */
    protected void objective(final String objectiveName, final ObjectiveFactory factory) {
        registeredFeatures.put(objectiveName, (name, api) -> api.objectives().registry().register(name, factory));
    }

    /**
     * Registers a player placeholder with the given name and factory.
     *
     * @param placeholderName the name of the placeholder that will identify it in the script
     * @param factory         the factory to create instances of the player placeholder
     */
    protected void playerPlaceholder(final String placeholderName, final PlayerPlaceholderFactory factory) {
        registeredFeatures.put(placeholderName, (name, api) -> api.placeholders().registry().register(name, factory));
    }

    /**
     * Registers a playerless placeholder with the specified name and factory.
     *
     * @param placeholderName the name of the placeholder that will identify it in the script
     * @param factory         the factory to create instances of the playerless placeholder
     */
    protected void playerlessPlaceholder(final String placeholderName, final PlayerlessPlaceholderFactory factory) {
        registeredFeatures.put(placeholderName, (name, api) -> api.placeholders().registry().register(name, factory));
    }

    /**
     * Registers a placeholder with the given name using the provided factory capable of creating
     * both player and playerless placeholders.
     *
     * @param placeholderName the name of the placeholder that will identify it in the script
     * @param factory         the factory to create both player and playerless placeholders
     * @param <F>             the type of the factory that extends both
     *                        {@link PlayerPlaceholderFactory} and {@link PlayerlessPlaceholderFactory}
     */
    protected <F extends PlayerPlaceholderFactory & PlayerlessPlaceholderFactory> void nullablePlaceholder(final String placeholderName, final F factory) {
        registeredFeatures.put(placeholderName, (name, api) -> api.placeholders().registry().registerCombined(name, factory));
    }

    /**
     * Registers an item with the given name and factory.
     *
     * @param itemName the name of the item that will identify it in the script
     * @param factory  the factory to create instances of the item
     */
    protected void item(final String itemName, final TypeFactory<QuestItemWrapper> factory) {
        registeredFeatures.put(itemName, (name, api) -> api.items().registry().register(name, factory));
    }

    /**
     * Registers an item with a serializer that defines how to convert an item into a string format.
     *
     * @param itemName   the name of the item that will identify it in the script
     * @param serializer the serializer used to convert {@code ItemStack} instances into
     *                   string format to be parsed as {@code QuestItem}
     */
    protected void item(final String itemName, final QuestItemSerializer serializer) {
        registeredFeatures.put(itemName, (name, api) -> api.items().registry().registerSerializer(name, serializer));
    }

    /**
     * Registers an item with a factory to create instances and a serializer to convert it to a string format.
     *
     * @param itemName   the name of the item that will identify it in the script
     * @param factory    the factory to create instances of the item
     * @param serializer the serializer to convert item instances into a string representation
     */
    protected void item(final String itemName, final TypeFactory<QuestItemWrapper> factory, final QuestItemSerializer serializer) {
        item(itemName, factory);
        item(itemName, serializer);
    }

    /**
     * Registers an NPC with the given name and factory.
     *
     * @param npcName the name of the NPC that will identify it in the script
     * @param factory the factory to create instances of the NPC
     */
    protected void npc(final String npcName, final NpcFactory factory) {
        registeredFeatures.put(npcName, (name, api) -> api.npcs().registry().register(name, factory));
    }

    /**
     * Registers an {@link NpcReverseIdentifier} to allow matching NPCs to their BetonQuest IDs.
     *
     * @param identifier the reverse identifier used to map or reference the NPC
     */
    protected void npc(final NpcReverseIdentifier identifier) {
        registeredFeatures.put("", (name, api) -> api.npcs().registry().registerIdentifier(identifier));
    }

    /**
     * Registers an NPC with the given name, factory, and reverse identifier.
     *
     * @param npcName    the name of the NPC that will identify it in the script
     * @param factory    the factory to create specific instances of the NPC
     * @param identifier the reverse identifier used to map or reference the NPC
     */
    protected void npc(final String npcName, final NpcFactory factory, final NpcReverseIdentifier identifier) {
        npc(npcName, factory);
        npc(identifier);
    }
}
