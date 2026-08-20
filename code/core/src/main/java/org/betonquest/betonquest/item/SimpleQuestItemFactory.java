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
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.text.TextParser;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The text parser used to parse text.
     */
    protected final TextParser textParser;

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
     * @param textParser            the text parser used to parse text
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the Localizations instance if the "quest item" lore line should be added
     */
    public SimpleQuestItemFactory(final TextParser textParser,
                                  final BookPageWrapper bookPageWrapper, final Supplier<Localizations> questItemLoreSupplier) {
        this.textParser = textParser;
        this.bookPageWrapper = bookPageWrapper;
        this.questItemLoreSupplier = questItemLoreSupplier;
    }

    /**
     * Parses the instruction string as Simple Quest Item.
     *
     * @param string the instruction string, starting with {@link DefaultBlockSelector}
     * @return the parsed QuestItem
     * @throws QuestException when an error occurs while parsing
     */
    @SuppressWarnings({"NullAway", "DataFlowIssue"})
    public QuestItem parseInstruction(final ArgumentParsers argumentParsers, final String string) throws QuestException {
        final Instruction instruction = new DefaultInstruction(PlaceholderProcessor.EMPTY_PLACEHOLDER, Map::of, null,
                null, argumentParsers, "simple " + string);
        return parseInstructionInternal(instruction);
    }

    /**
     * Parses the Quest Item from material and handler arguments.
     *
     * @param instruction the instruction for the Handlers
     * @return the parsed Quest Item
     * @throws QuestException when placeholders could not be resolved or handlers not be filled
     */
    protected QuestItem parseInstructionInternal(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();

        final NameHandler name = new NameHandler();

        final Localizations localizations = questItemLoreSupplier.get();
        final QuestHandler questHandler = new QuestHandler(localizations == null
                ? LoreConsumer.EMPTY : new LoreConsumer.Lore(localizations));
        final LoreHandler lore = new LoreHandler(questHandler::isLoreSet);
        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                questHandler,
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BannerHandler(),
                new BookHandler(textParser, bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        if (instruction.hasNext()) {
            fillHandler(handlers, instruction);
        }
        return new SimpleQuestItem(selector, handlers, name, lore);
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        return new ShallowWrapper(parseInstructionInternal(instruction));
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
        for (final ItemMetaHandler<?> handler : keyToHandler.values()) {
            handler.set(instruction);
        }
    }

    /**
     * A wrapper for a quest Item without placeholders to resolve.
     *
     * @param questItem the quest item to wrap.
     */
    public record ShallowWrapper(QuestItem questItem) implements QuestItemWrapper {

        @Override
        public QuestItem getItem(@Nullable final Profile profile) {
            return questItem;
        }
    }
}
