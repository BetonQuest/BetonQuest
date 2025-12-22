package org.betonquest.betonquest.api.instruction.chain;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgument;
import org.betonquest.betonquest.api.instruction.argument.DecoratedNumberArgument;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

public interface InstructionChainParser {

    /**
     * Default parser for {@link String}.
     *
     * @return a parser for strings
     */
    DecoratedArgument<String> string();

    /**
     * Default parser for {@link Boolean}.
     *
     * @return a parser for booleans
     */
    DecoratedArgument<Boolean> bool();

    /**
     * Default parser for {@link Vector}.
     *
     * @return a parser for vectors
     */
    DecoratedArgument<Vector> vector();

    /**
     * Default parser for {@link World}.
     *
     * @return a parser for worlds
     */
    DecoratedArgument<World> world();

    /**
     * Default parser for {@link Location}.
     *
     * @return a parser for locations
     */
    DecoratedArgument<Location> location();

    /**
     * Default parser for {@link Component}.
     *
     * @return a parser for components
     */
    DecoratedArgument<Component> component();

    /**
     * Default parser for {@link UUID}.
     *
     * @return a parser for UUIDs
     */
    DecoratedArgument<UUID> uuid();

    /**
     * Default parser for {@link Number} using {@link DecoratedNumberArgument}.
     *
     * @return a parser for numbers
     */
    DecoratedNumberArgument number();

    /**
     * Default parser for an {@link Enum} type.
     *
     * @param enumType the enum type to get a parser for
     * @param <E>      the enum type
     * @return a parser for enums of the given type
     */
    <E extends Enum<E>> DecoratedArgument<E> enumeration(Class<E> enumType);
}
