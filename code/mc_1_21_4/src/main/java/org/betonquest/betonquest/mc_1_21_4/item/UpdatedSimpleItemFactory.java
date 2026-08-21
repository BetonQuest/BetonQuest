package org.betonquest.betonquest.mc_1_21_4.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.item.LoreConsumer;
import org.betonquest.betonquest.item.SimpleQuestItem;
import org.betonquest.betonquest.item.SimpleQuestItemFactory;
import org.betonquest.betonquest.item.typehandler.BannerHandler;
import org.betonquest.betonquest.item.typehandler.BookHandler;
import org.betonquest.betonquest.item.typehandler.ColorHandler;
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
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedNameHandler;
import org.betonquest.betonquest.mc_1_20_6.item.UpdatedPotionHandler;

import java.util.List;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class UpdatedSimpleItemFactory extends SimpleQuestItemFactory {

    /**
     * Creates a new simple Quest Item Factory.
     *
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the Localizations instance if the "quest item" lore line should be added
     */
    public UpdatedSimpleItemFactory(final BookPageWrapper bookPageWrapper, final Supplier<Localizations> questItemLoreSupplier) {
        super(bookPageWrapper, questItemLoreSupplier);
    }

    @Override
    protected QuestItem parseInstructionInternal(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();

        final NameHandler name = new UpdatedNameHandler();

        final Localizations localizations = questItemLoreSupplier.get();
        final QuestHandler questHandler = new QuestHandler(localizations == null
                ? LoreConsumer.EMPTY : new LoreConsumer.Lore(localizations));
        final LoreHandler lore = new LoreHandler(questHandler::isLoreSet);

        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new UpdatedCustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                questHandler,
                new EnchantmentsHandler(),
                new UpdatedPotionHandler(),
                new BannerHandler(),
                new BookHandler(bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        if (instruction.hasNext()) {
            fillHandler(handlers, instruction);
        }
        return new SimpleQuestItem(selector, handlers, name, lore);
    }
}
