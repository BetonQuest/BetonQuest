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
public interface ArgumentParsers {

    /**
     * Default parser for {@link String}.
     *
     * @return a parser for strings
     */
    DecoratedArgumentParser<String> string();

    /**
     * Default parser for {@link Boolean}.
     *
     * @return a parser for booleans
     */
    DecoratedArgumentParser<Boolean> bool();

    /**
     * Default parser for {@link Vector}.
     *
     * @return a parser for vectors
     */
    DecoratedArgumentParser<Vector> vector();

    /**
     * Default parser for {@link World}.
     *
     * @return a parser for worlds
     */
    DecoratedArgumentParser<World> world();

    /**
     * Default parser for {@link Location}.
     *
     * @return a parser for locations
     */
    DecoratedArgumentParser<Location> location();

    /**
     * Default parser for {@link ItemWrapper}.
     *
     * @return a parser for quest items
     */
    DecoratedArgumentParser<ItemWrapper> item();

    /**
     * Default parser for a package identifier.
     *
     * @return a parser for package identifiers.
     */
    DecoratedArgumentParser<String> packageIdentifier();

    /**
     * Default parser for {@link Component}.
     *
     * @return a parser for components
     */
    DecoratedArgumentParser<Component> component();

    /**
     * Default parser for {@link UUID}.
     *
     * @return a parser for UUIDs
     */
    DecoratedArgumentParser<UUID> uuid();

    /**
     * Default parser for {@link Number} using {@link NumberArgumentParser}.
     *
     * @return a parser for numbers
     */
    NumberArgumentParser number();

    /**
     * Default parser for an {@link Enum} type.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a parser for enums of the given type
     */
    <E extends Enum<E>> DecoratedArgumentParser<E> forEnum(Class<E> enumType);
}
