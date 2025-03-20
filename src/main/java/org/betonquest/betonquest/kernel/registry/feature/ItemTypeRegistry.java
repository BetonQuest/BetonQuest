package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;
import org.betonquest.betonquest.kernel.registry.TypeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for {@link QuestItem} types.
 */
public class ItemTypeRegistry extends FactoryRegistry<TypeFactory<QuestItemWrapper>> {
    /**
     * Identifies registered serializer by string.
     */
    private final Map<String, QuestItemSerializer> serializers;

    /**
     * Create a new Item registry.
     *
     * @param log the logger that will be used for logging
     */
    public ItemTypeRegistry(final BetonQuestLogger log) {
        super(log, "items");
        serializers = new HashMap<>();
    }

    /**
     * Registers a {@link QuestItemSerializer} to allow parsing an ItemStack to instruction string.
     *
     * @param name       the name of the type
     * @param serializer the serializer to parse items to string
     */
    public void registerSerializer(final String name, final QuestItemSerializer serializer) {
        log.debug("Registering item serializer for '" + name + "' type");
        serializers.put(name, serializer);
    }

    /**
     * Fetches the serializer to parse ItemStacks into String format.
     *
     * @param name the name of the serializer
     * @return a factory to create the type
     * @throws QuestException when there is no serializer with that name registered
     */
    public QuestItemSerializer getSerializer(final String name) throws QuestException {
        final QuestItemSerializer serializer = serializers.get(name);
        if (serializer == null) {
            throw new QuestException("No serializer for '" + name + "' type");
        }
        return serializer;
    }

    /**
     * Gets the keys of all registered serializers.
     *
     * @return the actual key set
     */
    public Set<String> serializerKeySet() {
        return serializers.keySet();
    }
}
