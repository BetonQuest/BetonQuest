package org.betonquest.betonquest.api.instruction.chain;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * In this step of the instruction chain, the parser is decided.
 * At this stage only the targeted argument or argument key is known.
 *
 * @see ChainStarter
 * @see ChainRetriever
 * @see OptionalChainParser
 */
public interface PlainChainParser {

    /**
     * Returns {@link ChainRetriever} for the given {@link Argument} as parser.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the variable
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    <T> DecoratableChainRetriever<T> as(InstructionArgumentParser<T> argument);

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#string()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<String> string();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#bool()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Boolean> bool();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#vector()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Vector> vector();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#world()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<World> world();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#location()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Location> location();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#component()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Component> component();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#uuid()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<UUID> uuid();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#number()} as parser.
     *
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    NumberChainRetriever number();

    /**
     * Returns {@link ChainRetriever} with {@link ArgumentParsers#forEnum(Class)} as parser.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a new {@link ChainRetriever} carrying all previous settings
     */
    <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(Class<E> enumType);
}
