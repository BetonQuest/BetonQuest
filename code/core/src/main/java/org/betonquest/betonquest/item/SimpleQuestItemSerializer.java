package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Converts {@link ItemStack}s into the simple BQ format, parsable by a {@link SimpleQuestItemFactory}.
 */
public class SimpleQuestItemSerializer implements QuestItemSerializer {

    /**
     * Handlers for the {@link #serialize(ItemStack)} method.
     */
    private final SimpleQuestItemHandlerRegistry handlerRegistry;

    /**
     * Constructs a new Simple Serializer with {@link ItemMetaHandler}s.
     *
     * @param handlerRegistry the handler to use for serialization
     */
    public SimpleQuestItemSerializer(final SimpleQuestItemHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
    }

    @Override
    public String serialize(final ItemStack item) {
        if (!item.hasItemMeta()) {
            return item.getType().toString();
        }
        final ItemMeta meta = item.getItemMeta();
        final StringBuilder builder = new StringBuilder();
        final SimpleQuestItemHandlerRegistry.Baked baked = handlerRegistry.get();
        for (final ItemMetaHandler<? extends ItemMeta> handler : List.of(baked.name(), baked.lore())) {
            final String serialize = handler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }
        for (final ItemMetaHandler<? extends ItemMeta> staticHandler : baked.handlers()) {
            final String serialize = staticHandler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }

        return item.getType() + builder.toString();
    }
}
