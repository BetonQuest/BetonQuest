package org.betonquest.betonquest.mc_1_20_6.item;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.item.LoreConsumer;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.BannerHandler;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
import org.betonquest.betonquest.item.typehandler.CustomModelDataHandler;
import org.betonquest.betonquest.item.typehandler.DurabilityHandler;
import org.betonquest.betonquest.item.typehandler.EnchantmentsHandler;
import org.betonquest.betonquest.item.typehandler.FireworkHandler;
import org.betonquest.betonquest.item.typehandler.FlagHandler;
import org.betonquest.betonquest.item.typehandler.HeadHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Converts {@link ItemStack}s into the simple BQ format, parsable by a {@link SimpleQuestItemFactory}.
 */
public class UpdatedSimpleQuestItemSerializer extends SimpleQuestItemSerializer {

    /**
     * Constructs a new Simple Serializer with updated {@link ItemMetaHandler}s.
     *
     * @param bookPageWrapper the book page wrapper used to split pages
     */
    public UpdatedSimpleQuestItemSerializer(final BookPageWrapper bookPageWrapper) {
        super(List.of(
                new DurabilityHandler(), new UpdatedNameHandler(), new LoreHandler(new QuestHandler(LoreConsumer.EMPTY_ARGUMENT)), new EnchantmentsHandler(),
                new BookHandler(bookPageWrapper), new UpdatedPotionHandler(), new BannerHandler(), new ColorHandler(), new HeadHandler(),
                new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler(),
                new QuestHandler(LoreConsumer.EMPTY_ARGUMENT)
        ));
    }

    /**
     * Creates a simple item factory with the version specific handlers.
     *
     * @param config          the config to get values from
     * @param api             the BetonQuest api instance
     * @param bookPageWrapper the book page wrapper used to split pages
     * @return a new item factory with handlers for this version
     */
    public static SimpleQuestItemFactory create(final ConfigAccessor config, final BetonQuestApi api,
                                                final BookPageWrapper bookPageWrapper) {
        final QuestHandler questHandler = new QuestHandler(new LoreConsumer.SupplierArgument(
                () -> config.getBoolean("item.quest.lore") ? api.localizations() : null
        ));
        final UpdatedNameHandler name = new UpdatedNameHandler();
        final LoreHandler lore = new LoreHandler(questHandler);

        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                new EnchantmentsHandler(),
                new UpdatedPotionHandler(),
                new BannerHandler(),
                new BookHandler(bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );
        return new SimpleQuestItemFactory(name, lore, handlers);
    }
}
