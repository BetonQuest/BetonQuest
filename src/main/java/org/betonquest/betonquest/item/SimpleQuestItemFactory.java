package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
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
import org.betonquest.betonquest.kernel.registry.TypeFactory;
import org.betonquest.betonquest.util.BlockSelector;
import org.betonquest.betonquest.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Creates {@link SimpleQuestItem}s from {@link Instruction}s.
 */
public class SimpleQuestItemFactory implements TypeFactory<QuestItem> {

    /**
     * Creates a new simple Quest Item Factory.
     */
    public SimpleQuestItemFactory() {
    }

    /**
     * Parses the instruction string as Simple Quest Item.
     *
     * @param string the instruction string, starting with {@link BlockSelector}
     * @return the parsed QuestItem
     * @throws QuestException when an error occurs while parsing
     */
    public QuestItem parseInstruction(final String string) throws QuestException {
        final String[] split = string.split(" ");
        final String material = split[0];
        final List<String> arguments = split.length > 1 ? List.of(split).subList(1, split.length) : List.of();
        return parseInstruction(material, arguments);
    }

    private QuestItem parseInstruction(final String material, final List<String> arguments) throws QuestException {
        final BlockSelector selector = new BlockSelector(material);

        final NameHandler name = new NameHandler();
        final LoreHandler lore = new LoreHandler();

        final List<ItemMetaHandler<?>> handlers = List.of(
                new DurabilityHandler(),
                new CustomModelDataHandler(),
                new UnbreakableHandler(),
                new FlagHandler(),
                name,
                lore,
                new EnchantmentsHandler(),
                new PotionHandler(),
                new BookHandler(),
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
    public QuestItem parseInstruction(final Instruction rawInstruction) throws QuestException {
        final String instructionString = rawInstruction.get(rawInstruction.toString(), Argument.STRING).getValue(null);
        final Instruction instruction = new Instruction(rawInstruction.getPackage(), rawInstruction.getID(), instructionString);
        final String material = instruction.next();
        final List<String> arguments;
        if (instruction.hasNext()) {
            final List<String> valueParts = instruction.getValueParts();
            arguments = valueParts.subList(1, valueParts.size());
        } else {
            arguments = List.of();
        }
        return parseInstruction(material, arguments);
    }

    private void fillHandler(final List<ItemMetaHandler<?>> handlers, final List<String> arguments) throws QuestException {
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
}
