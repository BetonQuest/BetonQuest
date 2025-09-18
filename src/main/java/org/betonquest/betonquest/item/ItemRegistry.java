package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.kernel.FeatureTypeRegistry;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.Set;

/**
 * Stores the item factories and serializer.
 */
public interface ItemRegistry extends FeatureTypeRegistry<QuestItemWrapper> {
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
