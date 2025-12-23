package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.parser.DefaultArgumentParsers;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
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
import org.betonquest.betonquest.util.BlockSelector;
import org.betonquest.betonquest.util.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The quest package manager to get quest packages from.
     */
    protected final QuestPackageManager packManager;

    /**
     * The text parser used to parse text.
     */
    protected final TextParser textParser;

    /**
     * The book page wrapper used to split pages.
     */
    protected final BookPageWrapper bookPageWrapper;

    /**
     * Supplier for the PluginMessage.
     */
    protected final Supplier<PluginMessage> questItemLoreSupplier;

    /**
     * Variable processor to create and resolve variables.
     */
    private final Variables variables;

    /**
     * Creates a new simple Quest Item Factory.
     *
     * @param variables             the variable processor to create and resolve variables
     * @param packManager           the quest package manager to get quest packages from
     * @param textParser            the text parser used to parse text
     * @param bookPageWrapper       the book page wrapper used to split pages
     * @param questItemLoreSupplier supplies the plugin message instance if the "quest item" lore line should be added
     */
    public SimpleQuestItemFactory(final Variables variables, final QuestPackageManager packManager, final TextParser textParser,
                                  final BookPageWrapper bookPageWrapper, final Supplier<PluginMessage> questItemLoreSupplier) {
        this.variables = variables;
        this.packManager = packManager;
        this.textParser = textParser;
        this.bookPageWrapper = bookPageWrapper;
        this.questItemLoreSupplier = questItemLoreSupplier;
    }

    /**
     * Parses the instruction string as Simple Quest Item.
     *
     * @param string the instruction string, starting with {@link BlockSelector}
     * @return the parsed QuestItem
     * @throws QuestException when an error occurs while parsing
     */
    public org.betonquest.betonquest.api.item.QuestItem parseInstruction(final String string) throws QuestException {
        final String[] split = string.split(" ");
        final String material = split[0];
        final List<String> arguments = split.length > 1 ? List.of(split).subList(1, split.length) : List.of();
        return parseInstruction(material, arguments);
    }

    /**
     * Parses the Quest Item from material and handler arguments.
     *
     * @param material  the {@link BlockSelector} string
     * @param arguments the arguments for the Handlers
     * @return the parsed Quest Item
     * @throws QuestException when variables could not be resolved or handlers not be filled
     */
    protected org.betonquest.betonquest.api.item.QuestItem parseInstruction(final String material, final List<String> arguments) throws QuestException {
        final BlockSelector selector = new BlockSelector(material);

        final NameHandler name = new NameHandler(textParser);
        final LoreHandler lore = new LoreHandler(textParser);

        final PluginMessage pluginMessage = questItemLoreSupplier.get();
        final List<ItemMetaHandler<?>> handlers = List.of(
                new QuestHandler(pluginMessage == null ? QuestHandler.LoreConsumer.EMPTY : new QuestHandler.Lore(pluginMessage)),
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BookHandler(textParser, bookPageWrapper),
                new HeadHandler(),
                new ColorHandler(),
                new FireworkHandler()
        );

        if (!arguments.isEmpty()) {
            fillHandler(handlers, arguments);
        }
        return new SimpleQuestItem(selector, handlers, name, lore);
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction rawInstruction) throws QuestException {
        final String instructionString = rawInstruction.get(rawInstruction.toString(), rawInstruction.getParsers().string()).getValue(null);
        final Instruction instruction = new DefaultInstruction(variables, packManager, rawInstruction.getPackage(),
                rawInstruction.getID(), DefaultArgumentParsers.INSTANCE, instructionString);
        final String material = instruction.nextElement();
        final List<String> arguments;
        if (instruction.hasNext()) {
            final List<String> valueParts = instruction.getValueParts();
            arguments = valueParts.subList(1, valueParts.size());
        } else {
            arguments = List.of();
        }
        return new ShallowWrapper(parseInstruction(material, arguments));
    }

    /**
     * Fills the handlers with arguments.
     *
     * @param handlers  the handlers to fill
     * @param arguments the instruction arguments to fill into the handlers
     * @throws QuestException when the argument is invalid for a handler or no handler accepts that argument
     */
    protected void fillHandler(final List<ItemMetaHandler<?>> handlers, final List<String> arguments) throws QuestException {
        final Map<String, ItemMetaHandler<?>> keyToHandler = new HashMap<>();
        for (final ItemMetaHandler<?> handler : handlers) {
            for (final String key : handler.keys()) {
                keyToHandler.put(key, handler);
            }
        }
        for (final String part : arguments) {
            if (part.isEmpty()) {
                continue; //catch empty string caused by multiple whitespaces in instruction split
            }

            final String argumentName = getArgumentName(part.toLowerCase(Locale.ROOT));
            final String data = getArgumentData(part);

            final ItemMetaHandler<?> handler = Utils.getNN(keyToHandler.get(argumentName), "Unknown argument: " + argumentName);
            handler.set(argumentName, data);
        }
    }

    /**
     * Returns the data behind the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the data behind the argument name
     */
    private String getArgumentData(final String argument) {
        return argument.substring(argument.indexOf(':') + 1);
    }

    /**
     * Returns the argument name.
     * If the argument does not contain a colon, it returns the full argument.
     *
     * @param argument the full argument
     * @return the argument name
     */
    private String getArgumentName(final String argument) {
        if (argument.contains(":")) {
            return argument.substring(0, argument.indexOf(':'));
        }
        return argument;
    }

    /**
     * A wrapper for a quest Item without variables to resolve.
     *
     * @param questItem the quest item to wrap.
     */
    public record ShallowWrapper(org.betonquest.betonquest.api.item.QuestItem questItem) implements QuestItemWrapper {

        @Override
        public org.betonquest.betonquest.api.item.QuestItem getItem(@Nullable final Profile profile) {
            return questItem;
        }
    }
}
