package org.betonquest.betonquest.item;

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
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Converts {@link ItemStack}s into the simple BQ format.
 */
public class SimpleQuestItemSerializer implements QuestItemSerializer {
    /**
     * Handlers for the {@link #serialize(ItemStack)} method.
     */
    private static final List<ItemMetaHandler<? extends ItemMeta>> STATIC_HANDLERS = List.of(
            new DurabilityHandler(), new NameHandler(), new LoreHandler(), new EnchantmentsHandler(),
            new BookHandler(), new PotionHandler(), new ColorHandler(), new HeadHandler(),
            new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler()
    );

    /**
     * The empty default constructor.
     */
    public SimpleQuestItemSerializer() {
    }

    @Override
    public String serialize(final ItemStack item) {
        if (!item.hasItemMeta()) {
            return item.getType().toString();
        }
        final ItemMeta meta = item.getItemMeta();
        final StringBuilder builder = new StringBuilder();
        for (final ItemMetaHandler<? extends ItemMeta> staticHandler : STATIC_HANDLERS) {
            final String serialize = staticHandler.rawSerializeToString(meta);
            if (serialize != null) {
                builder.append(' ').append(serialize);
            }
        }

        return item.getType() + builder.toString();
    }
}
