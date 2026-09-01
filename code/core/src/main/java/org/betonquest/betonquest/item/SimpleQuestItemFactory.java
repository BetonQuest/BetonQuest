package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.QuestException;
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
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.util.DefaultBlockSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates {@link SimpleQuestItemWrapper}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * Name meta handlers.
     */
    private final SimpleQuestItemHandlerRegistry handlerRegistry;

    /**
     * Creates a new simple Quest Item Factory with custom handlers.
     *
     * @param handlerRegistry the handler to use for deserialization
     */
    public SimpleQuestItemFactory(final SimpleQuestItemHandlerRegistry handlerRegistry) {
        this.handlerRegistry = handlerRegistry;
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

        final SimpleQuestItemHandlerRegistry.Baked baked = handlerRegistry.get();

        final NameMetaHandler.NameAttribute nameAttribute = baked.name().parse(instruction);
        final LoreMetaHandler.LoreAttribute loreAttribute = baked.lore().parse(instruction);
        final List<Attribute> attributes = new ArrayList<>();
        if (instruction.hasNext()) {
            for (final ItemMetaHandler<?> handler : baked.handlers()) {
                final Attribute attribute = handler.parse(instruction);
                if (attribute != null) {
                    attributes.add(attribute);
                }
            }
        }
        return new SimpleQuestItemWrapper(selector, nameAttribute, loreAttribute, attributes);
    }
}
