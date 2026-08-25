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
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.LoreMetaHandler;
import org.betonquest.betonquest.item.handler.NameMetaHandler;
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
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.item.typehandler.PotionHandler;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.betonquest.betonquest.item.typehandler.UnbreakableHandler;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.util.DefaultBlockSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItemWrapper}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * Name meta handler to for {@link QuestItem#getName()}.
     */
    protected final NameMetaHandler nameHandler;

    /**
     * Lore meta handler to for {@link QuestItem#getLore()}.
     */
    protected final LoreMetaHandler loreHandler;

    /**
     * All other meta handler to parse attributes of the simple item.
     */
    protected final List<ItemMetaHandler<?>> handlers;

    /**
     * Creates a new simple Quest Item Factory with custom handlers.
     *
     * @param nameHandler the name meta handler for {@link QuestItem#getName()}
     * @param loreHandler the lore meta handler for {@link QuestItem#getLore()}
     * @param handlers    the handler which allow defining items, exclusive the name and lore handler
     */
    public SimpleQuestItemFactory(final NameMetaHandler nameHandler, final LoreMetaHandler loreHandler,
                                  final List<ItemMetaHandler<?>> handlers) {
        this.nameHandler = nameHandler;
        this.loreHandler = loreHandler;
        this.handlers = handlers;
    }

    /**
     * Creates a new simple Quest Item Factory.
     *
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the Localizations instance if the "quest item" lore line should be added
     */
    public SimpleQuestItemFactory(final BookPageWrapper bookPageWrapper, final Supplier<Localizations> questItemLoreSupplier) {
        this.nameHandler = new NameHandler();

        final Localizations localizations = questItemLoreSupplier.get();
        final QuestHandler questHandler = new QuestHandler(localizations == null
                ? LoreConsumer.EMPTY_ARGUMENT : new LoreConsumer.LoreArgument(localizations));
        this.loreHandler = new LoreHandler(questHandler);
        this.handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BannerHandler(),
                new BookHandler(bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );
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
        return parseInstruction(instruction).getItem(null);
    }

    /**
     * Parses the Quest Item from material and handler arguments.
     *
     * @param instruction the instruction for the Handlers
     * @return the parsed Quest Item
     * @throws QuestException when placeholders could not be resolved or handlers not be filled
     */
    @Override
    public SimpleQuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<BlockSelector> selector = instruction.blockSelector().get();

        final NameMetaHandler.NameAttribute nameAttribute = nameHandler.parse(instruction);
        final LoreMetaHandler.LoreAttribute loreAttribute = loreHandler.parse(instruction);
        final List<Attribute<?>> attributes = new ArrayList<>();
        if (instruction.hasNext()) {
            for (final ItemMetaHandler<?> handler : handlers) {
                final Attribute<?> attribute = handler.parse(instruction);
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
        }
        return new SimpleQuestItemWrapper(selector, nameAttribute, loreAttribute, attributes);
    }
}
