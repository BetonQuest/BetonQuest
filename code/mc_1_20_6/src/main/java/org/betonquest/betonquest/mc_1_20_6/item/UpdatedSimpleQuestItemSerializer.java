package org.betonquest.betonquest.mc_1_20_6.item;

import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.SimpleQuestItemSerializer;
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
     * @param textParser      the text parser used to parse text
     * @param bookPageWrapper the book page wrapper used to split pages
     */
    public UpdatedSimpleQuestItemSerializer(final TextParser textParser, final BookPageWrapper bookPageWrapper) {
        super(List.of(
                new DurabilityHandler(), new NameHandler(textParser), new LoreHandler(textParser), new EnchantmentsHandler(),
                new BookHandler(textParser, bookPageWrapper), new UpdatedPotionHandler(), new ColorHandler(), new HeadHandler(),
                new FireworkHandler(), new UnbreakableHandler(), new CustomModelDataHandler(), new FlagHandler(),
                new QuestHandler(QuestHandler.LoreConsumer.EMPTY)
        ));
    }
}
