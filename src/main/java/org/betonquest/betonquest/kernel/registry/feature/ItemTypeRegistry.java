package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.ItemRegistry;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemSerializer;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for {@link QuestItem} types.
 */
public class ItemTypeRegistry extends FactoryRegistry<TypeFactory<QuestItemWrapper>> implements ItemRegistry {
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

    @Override
    public void registerSerializer(final String name, final QuestItemSerializer serializer) {
        log.debug("Registering item serializer for '" + name + "' type");
        serializers.put(name, serializer);
    }

    @Override
    public QuestItemSerializer getSerializer(final String name) throws QuestException {
        final QuestItemSerializer serializer = serializers.get(name);
        if (serializer == null) {
            throw new QuestException("No serializer for '" + name + "' type");
        }
        return serializer;
    }

    @Override
    public Set<String> serializerKeySet() {
        return serializers.keySet();
    }
}
