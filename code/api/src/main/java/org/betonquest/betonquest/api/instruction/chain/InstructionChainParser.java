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
 * @since 3.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface InstructionChainParser {

    /**
     * Returns {@link DecoratableChainRetriever} for the given {@link InstructionArgumentParser} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is disabled by default.
     *
     * @param argument the argument parser to use
     * @param <T>      the type of the argument
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    <T> DecoratableChainRetriever<T> parse(InstructionArgumentParser<T> argument);

    /**
     * Returns {@link DecoratableChainRetriever} for the given {@link InstructionArgumentParser} as parser.
     * Forwards to {@link #parse(InstructionArgumentParser)} with the {@link SimpleArgumentParser} by default.
     *
     * @param argument the simplified argument parser to use
     * @param <T>      the type of the argument
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    default <T> DecoratableChainRetriever<T> parse(final SimpleArgumentParser<T> argument) {
        return this.parse((InstructionArgumentParser<T>) argument);
    }

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#string()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<String> string();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#bool()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<Boolean> bool();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#vector()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<Vector> vector();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#world()} as parser.
     * <p>
     * <b>World objects should never be stored persistent to prevent memory leaks when they are unloaded!</b>
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<World> world();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#location()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<Location> location();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#item()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<ItemWrapper> item();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#blockSelector()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<BlockSelector> blockSelector();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#packageIdentifier()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<String> packageIdentifier();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#namespacedKey()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<NamespacedKey> namespacedKey();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#component()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<Component> component();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#uuid()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    DecoratableChainRetriever<UUID> uuid();

    /**
     * Returns {@link NumberChainRetriever} with {@link ArgumentParsers#number()} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link NumberChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    NumberChainRetriever number();

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#forEnum(Class)} as parser.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a new {@link DecoratableChainRetriever} carrying all previous settings
     * @since 3.0.0
     */
    <E extends Enum<E>> DecoratableChainRetriever<E> enumeration(Class<E> enumType);

    /**
     * Returns {@link DecoratableChainRetriever} with {@link ArgumentParsers#forIdentifier(Class)} to parse the argument.
     * <p>
     * The {@link DecoratableChainRetriever#cache(boolean) caching} is enabled by default.
     *
     * @param identifierClass the identifier class to parse
     * @param <I>             the identifier type
     * @return a new {@link DecoratableChainRetriever} for the argument with the identifier parser
     * @since 3.0.0
     */
    <I extends Identifier> DecoratableChainRetriever<I> identifier(Class<I> identifierClass);
}
