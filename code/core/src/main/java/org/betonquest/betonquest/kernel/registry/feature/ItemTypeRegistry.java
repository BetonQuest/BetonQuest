package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.item.LoreConsumer;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.kernel.registry.FactoryTypeRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for {@link QuestItem} types.
 */
public class ItemTypeRegistry extends FactoryTypeRegistry<QuestItemWrapper> implements ItemRegistry {

    /**
     * Identifies registered serializer by string.
     */
    private final Map<String, QuestItemSerializer> serializers;

    /**
     * Consumer to (possibly) add the "Quest Item"-Lore to the generated item.
     */
    private final LoreConsumer loreConsumer;

    /**
     * Create a new Item registry.
     *
     * @param log           the logger that will be used for logging
     * @param localizations the Localizations to get the lore line
     * @param config        the config for determining if the "Quest Item"-Lore should be generated on the item
     */
    public ItemTypeRegistry(final BetonQuestLogger log, final Localizations localizations, final ConfigAccessor config) {
        super(log, "items");
        final LoreConsumer.Lore lore = new LoreConsumer.Lore(localizations);
        this.loreConsumer = config.getBoolean("item.quest.lore") ? lore : LoreConsumer.EMPTY;
        serializers = new HashMap<>();
    }

    @Override
    public void register(final String name, final TypeFactory<QuestItemWrapper> factory) {
        super.register(name, new WrappedFactory(factory, loreConsumer));
    }

    @Override
    public void register(final String name, final TypeFactory<QuestItemWrapper> factory, final boolean ignoreQuestTag) {
        if (ignoreQuestTag) {
            super.register(name, factory);
        } else {
            register(name, factory);
        }
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

    /**
     * Factory wrapper to add the "quest-item" tag to all item factories.
     *
     * @param originalFactory the original factory creating the item
     * @param loreConsumer    the Consumer to (possibly) add the "Quest Item"-Lore to the generated item
     */
    private record WrappedFactory(TypeFactory<QuestItemWrapper> originalFactory,
                                  LoreConsumer loreConsumer) implements TypeFactory<QuestItemWrapper> {

        @Override
        public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
            final QuestItemWrapper wrapper = originalFactory.parseInstruction(instruction);
            final boolean isQuestItem = instruction.bool().getFlag("quest-item", true)
                    .getValue(null).orElse(false);
            return isQuestItem ? new QuestItemTagAdapterWrapper(wrapper, loreConsumer) : wrapper;
        }
    }
}
