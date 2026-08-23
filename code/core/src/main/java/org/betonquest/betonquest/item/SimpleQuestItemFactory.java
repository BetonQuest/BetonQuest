package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.item.typehandler.BannerHandler;
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
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.util.DefaultBlockSelector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The book page wrapper used to split pages.
     */
    protected final BookPageWrapper bookPageWrapper;

    /**
     * Supplier for the Localizations.
     */
    protected final Supplier<Localizations> questItemLoreSupplier;

    /**
     * Creates a new simple Quest Item Factory.
     *
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the Localizations instance if the "quest item" lore line should be added
     */
    public SimpleQuestItemFactory(final BookPageWrapper bookPageWrapper, final Supplier<Localizations> questItemLoreSupplier) {
        this.bookPageWrapper = bookPageWrapper;
        this.questItemLoreSupplier = questItemLoreSupplier;
    }

    /**
     * Parses the instruction string as Simple Quest Item.
     * <p>
     * This method exists solely for the database migration and will fail blatantly when using any placeholder or newer
     * feature in the instruction string.
     *
     * @param argumentParsers the argument parsers used for creating a new instruction
     * @param string          the instruction string, starting with {@link DefaultBlockSelector}
     * @return the parsed QuestItem
     * @throws QuestException when an error occurs while parsing
     */
    @SuppressWarnings({"NullAway", "DataFlowIssue"})
    public QuestItem parseInstruction(final ArgumentParsers argumentParsers, final String string) throws QuestException {
        final Instruction instruction = new DefaultInstruction(PlaceholderProcessor.EMPTY_PLACEHOLDER, Map::of, null,
                null, argumentParsers, "simple " + string);
        return parseInstructionInternal(instruction).getItem(null);
    }

    /**
     * Parses the Quest Item from material and handler arguments.
     *
     * @param instruction the instruction for the Handlers
     * @return the parsed Quest Item
     * @throws QuestException when placeholders could not be resolved or handlers not be filled
     */
    protected SimpleQuestItem parseInstructionInternal(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();

        final NameHandler name = new NameHandler();

        final Localizations localizations = questItemLoreSupplier.get();
        final QuestHandler questHandler = new QuestHandler(localizations == null
                ? LoreConsumer.EMPTY_ARGUMENT : new LoreConsumer.LoreArgument(localizations));
        final LoreHandler lore = new LoreHandler(questHandler::isLoreSet);
        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                questHandler,
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BannerHandler(),
                new BookHandler(bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        if (instruction.hasNext()) {
            fillHandler(handlers, instruction);
            fillHandler(List.of(name, lore), instruction);
        }
        return new SimpleQuestItem(selector, name, lore, handlers);
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        return parseInstructionInternal(instruction);
    }

    /**
     * Fills the handlers with arguments.
     *
     * @param handlers    the handlers to fill
     * @param instruction the instruction to fill into the handlers
     * @throws QuestException when the argument is invalid for a handler or no handler accepts that argument
     */
    protected void fillHandler(final List<ItemMetaHandler<?>> handlers, final Instruction instruction) throws QuestException {
        final Map<String, ItemMetaHandler<?>> keyToHandler = new HashMap<>();
        for (final ItemMetaHandler<?> handler : handlers) {
            for (final String key : handler.keys()) {
                keyToHandler.put(key, handler);
            }
        }
        for (final ItemMetaHandler<?> handler : keyToHandler.values()) { // TODO fix call amount logic
            handler.set(instruction);
        }
    }
}
