package org.betonquest.betonquest.api.instruction.chain;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * In the first step of the instruction chain, the parser is decided.
 * At this stage only the instruction starting the chain is known.
 *
 * @see InstructionChainRetriever
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface InstructionChainParser {

    /**
     * Returns {@link DecoratableChainRetriever} for the given {@link InstructionArgumentParser} as parser.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the argument
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    <T> DecoratableChainRetriever<T> parse(InstructionArgumentParser<T> argument);

    /**
     * Returns {@link DecoratableChainRetriever} for the given {@link InstructionArgumentParser} as parser.
     * Forwards to {@link #parse(InstructionArgumentParser)} with the {@link SimpleArgumentParser} by default.
     *
     * @param argument the simplified argument parser to use
     * @param <T>      the type of the argument
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    default <T> DecoratableChainRetriever<T> parse(final SimpleArgumentParser<T> argument) {
        return this.parse((InstructionArgumentParser<T>) argument);
    }

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#string()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<String> string();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#bool()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Boolean> bool();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#vector()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Vector> vector();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#world()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<World> world();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#location()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Location> location();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#item()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<ItemWrapper> item();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#blockSelector()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<BlockSelector> blockSelector();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#packageIdentifier()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<String> packageIdentifier();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#namespacedKey()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<NamespacedKey> namespacedKey();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#component()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Component> component();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#uuid()} as parser.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<UUID> uuid();

    /**
     * Returns {@link NumberChainRetriever} with {@link ArgumentParsers#number()} as parser.
     *
     * @return a new {@link NumberChainRetriever} carrying all previous settings
     */
    NumberChainRetriever number();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#forEnum(Class)} as parser.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     */
    <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(Class<E> enumType);

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#forIdentifier(Class)} to parse the argument.
     *
     * @param identifierClass the identifier class to parse
     * @param <I>             the identifier type
     * @return a new {@link DecoratableChainRetriever} for the argument with the identifier parser
     */
    <I extends Identifier> DecoratableChainRetriever<I> identifier(Class<I> identifierClass);
}
