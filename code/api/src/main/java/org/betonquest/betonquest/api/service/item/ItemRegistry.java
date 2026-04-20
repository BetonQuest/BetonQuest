package org.betonquest.betonquest.api.service.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.TypeFactory;

import java.util.Set;

/**
 * Stores the item factories and serializer.
 */
public interface ItemRegistry extends FeatureTypeRegistry<QuestItemWrapper> {

    /**
     * Registers an item factory with an additional quest item wrapper.
     * <p>
     * The wrapper will handle the {@code quest-item} tag and their lore configuration.
     *
     * @param name    the name of the type
     * @param factory the factory to create the type
     */
    @Override
    void register(String name, TypeFactory<QuestItemWrapper> factory);

    /**
     * Registers an item factory with an additional quest item wrapper.
     * <p>
     * The wrapper will handle the {@code quest-item} tag and their lore configuration.
     * Setting the {@param ignoreQuestTag} will not add the wrapper.
     *
     * @param name           the name of the type
     * @param factory        the factory to create the type
     * @param ignoreQuestTag if the quest item wrapper should not be added
     */
    void register(String name, TypeFactory<QuestItemWrapper> factory, boolean ignoreQuestTag);

    /**
     * Registers a {@link QuestItemSerializer} to allow parsing an ItemStack to instruction string.
     *
     * @param name       the name of the type
     * @param serializer the serializer to parse items to string
     */
    void registerSerializer(String name, QuestItemSerializer serializer);

    /**
     * Fetches the serializer to parse ItemStacks into String format.
     *
     * @param name the name of the serializer
     * @return a factory to create the type
     * @throws QuestException when there is no serializer with that name registered
     */
    QuestItemSerializer getSerializer(String name) throws QuestException;

    /**
     * Gets the keys of all registered serializers.
     *
     * @return the actual key set
     */
    Set<String> serializerKeySet();
}
