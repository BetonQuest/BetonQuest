package org.betonquest.betonquest.api.instruction.chain;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * In the first step of the instruction chain, the parser is decided.
 * At this stage only the instruction starting the chain is known.
 *
 * @see InstructionChainRetriever
 */
public interface InstructionChainParser {

    /**
     * Returns {@link InstructionChainRetriever} for the given {@link InstructionArgumentParser} as parser.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the variable
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    <T> DecoratableChainRetriever<T> parse(InstructionArgumentParser<T> argument);

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#string()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<String> string();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#bool()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Boolean> bool();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#vector()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Vector> vector();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#world()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<World> world();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#location()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Location> location();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#component()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<Component> component();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#uuid()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    DecoratableChainRetriever<UUID> uuid();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#number()} as parser.
     *
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    NumberChainRetriever number();

    /**
     * Returns {@link InstructionChainRetriever} with {@link ArgumentParsers#forEnum(Class)} as parser.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a new {@link InstructionChainRetriever} carrying all previous settings
     */
    <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(Class<E> enumType);
}
