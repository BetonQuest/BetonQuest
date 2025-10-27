package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
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
    private final List<ItemMetaHandler<? extends ItemMeta>> handlers;

    /**
     * Constructs a new Simple Serializer with standard {@link ItemMetaHandler}s.
     *
     * @param textParser      the text parser used to parse text
     * @param bookPageWrapper the book page wrapper used to split pages
     */
    public SimpleQuestItemSerializer(final TextParser textParser, final BookPageWrapper bookPageWrapper) {
        this(List.of(
                new DurabilityHandler(), new NameHandler(textParser), new LoreHandler(textParser), new EnchantmentsHandler(),
                new BookHandler(textParser, bookPageWrapper), new PotionHandler(), new ColorHandler(), new HeadHandler(),
                new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler(),
                new QuestHandler(QuestHandler.LoreConsumer.EMPTY)
        ));
    }

    /**
     * Constructs a new Simple Serializer with {@link ItemMetaHandler}s.
     *
     * @param handlers the handler to use for serialization
     */
    public SimpleQuestItemSerializer(final List<ItemMetaHandler<? extends ItemMeta>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public String serialize(final ItemStack item) {
        if (!item.hasItemMeta()) {
            return item.getType().toString();
        }
        final ItemMeta meta = item.getItemMeta();
        final StringBuilder builder = new StringBuilder();
        for (final ItemMetaHandler<? extends ItemMeta> staticHandler : handlers) {
            final String serialize = staticHandler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }

        return item.getType() + builder.toString();
    }
}
