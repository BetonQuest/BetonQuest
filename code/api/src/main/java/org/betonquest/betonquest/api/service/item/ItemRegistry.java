package org.betonquest.betonquest.api.service.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.jetbrains.annotations.Contract;

import java.util.Set;

/**
 * A registry for item factories and serializers.
 *
 * @since 3.0.0
 */
public interface ItemRegistry extends FeatureTypeRegistry<QuestItemWrapper> {

    /**
     * Registers an item factory with a quest item wrapper.
     * <p>
     * The wrapper handles the {@code quest-item} tag and lore configuration.
     *
     * @param name    the name of the item type
     * @param factory the factory to create the item type
     * @since 3.0.0
     */
    @Override
    void register(String name, TypeFactory<QuestItemWrapper> factory);

    /**
     * Registers an item factory with an optional quest item wrapper.
     * <p>
     * The wrapper handles the {@code quest-item} tag and lore configuration.
     * If {@code ignoreQuestTag} is true, the quest item wrapper is not added.
     *
     * @param name           the name of the item type
     * @param factory        the factory to create the item type
     * @param ignoreQuestTag whether to skip adding the quest item wrapper
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void register(String name, TypeFactory<QuestItemWrapper> factory, boolean ignoreQuestTag);

    /**
     * Registers a {@link QuestItemSerializer} to allow serializing an {@code ItemStack} into an instruction string.
     *
     * @param name       the name of the item type
     * @param serializer the serializer to parse items to strings
     * @since 3.0.0
     */
    @Contract(mutates = "this")
    void registerSerializer(String name, QuestItemSerializer serializer);

    /**
     * Returns the serializer for the specified name.
     *
     * @param name the name of the serializer to retrieve
     * @return the serializer instance
     * @throws QuestException if no serializer is registered under the given name
     * @since 3.0.0
     */
    @Contract(pure = true)
    QuestItemSerializer getSerializer(String name) throws QuestException;

    /**
     * Returns a set containing the keys of all registered serializers.
     *
     * @return the set of serializer keys
     * @since 3.0.0
     */
    @Contract(pure = true)
    Set<String> serializerKeySet();
}
