package org.betonquest.betonquest.api.instruction.argument;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * This offers implementations for {@link DecoratedArgumentParser} to parse common types.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ArgumentParsers {

    /**
     * Default {@link DecoratedArgumentParser} for {@link String}.
     *
     * @return a parser for strings
     */
    DecoratedArgumentParser<String> string();

    /**
     * Default {@link DecoratedArgumentParser} for {@link Boolean}.
     *
     * @return a parser for booleans
     */
    DecoratedArgumentParser<Boolean> bool();

    /**
     * Default {@link DecoratedArgumentParser} for {@link Vector}.
     *
     * @return a parser for vectors
     */
    DecoratedArgumentParser<Vector> vector();

    /**
     * Default {@link DecoratedArgumentParser} for {@link World}.
     *
     * @return a parser for worlds
     */
    DecoratedArgumentParser<World> world();

    /**
     * Default {@link DecoratedArgumentParser} for {@link Location}.
     *
     * @return a parser for locations
     */
    DecoratedArgumentParser<Location> location();

    /**
     * Default {@link DecoratedArgumentParser} for {@link ItemWrapper}.
     *
     * @return a parser for items
     */
    DecoratedArgumentParser<ItemWrapper> item();

    /**
     * Default {@link DecoratedArgumentParser} for a package identifier.
     * This parser simply expands the existing string value to a full package identifier
     * using the instruction's package.
     *
     * @return a parser for package identifiers.
     */
    DecoratedArgumentParser<String> packageIdentifier();

    /**
     * Default {@link DecoratedArgumentParser} for {@link Component}.
     *
     * @return a parser for components
     */
    DecoratedArgumentParser<Component> component();

    /**
     * Default {@link DecoratedArgumentParser} for {@link UUID}.
     *
     * @return a parser for UUIDs
     */
    DecoratedArgumentParser<UUID> uuid();

    /**
     * Default {@link NumberArgumentParser} for {@link Number}.
     *
     * @return a parser for numbers
     */
    NumberArgumentParser number();

    /**
     * Default {@link DecoratedArgumentParser} for an {@link Enum} type.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a parser for enums of the given type
     */
    <E extends Enum<E>> DecoratedArgumentParser<E> forEnum(Class<E> enumType);
}
