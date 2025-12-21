package org.betonquest.betonquest.api.instruction.argument.parser;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratableArgument;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgument;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * This offers default implementations for {@link Argument} to parse common types.
 */
public class DefaultArgumentParsers implements ArgumentParsers {

    /**
     * The default decoratable instance of {@link StringParser}.
     */
    public static final DecoratableArgument<String> STRING = new DecoratableArgument<>(new StringParser());

    /**
     * The default instance of {@link BooleanParser}.
     */
    public static final DecoratableArgument<Boolean> BOOLEAN = new DecoratableArgument<>(new BooleanParser());

    /**
     * The default instance of {@link VectorParser}.
     */
    public static final DecoratableArgument<Vector> VECTOR = new DecoratableArgument<>(new VectorParser());

    /**
     * The default instance of {@link WorldParser}.
     */
    public static final DecoratableArgument<World> WORLD = new DecoratableArgument<>(new WorldParser(Bukkit.getServer()));

    /**
     * The default instance of {@link LocationParser}.
     */
    public static final DecoratableArgument<Location> LOCATION = new DecoratableArgument<>(new LocationParser(Bukkit.getServer()));

    /**
     * The default instance of {@link NumberParser}.
     */
    public static final DecoratableArgument<Number> NUMBER = new DecoratableArgument<>(new NumberParser());

    /**
     * The default instance of {@link NumberParser} that checks if the number is not less than zero.
     */
    public static final NumberParser NUMBER_NOT_LESS_THAN_ZERO = new NumberParser(value -> {
        if (value.doubleValue() < 0) {
            throw new QuestException("Value must be greater than or equal to 0: " + value);
        }
    });

    /**
     * The default instance of {@link NumberParser} that checks if the number is not less than one.
     */
    public static final NumberParser NUMBER_NOT_LESS_THAN_ONE = new NumberParser(value -> {
        if (value.doubleValue() <= 0) {
            throw new QuestException("Value must be greater than or equal to 1: " + value);
        }
    });

    /**
     * The default instance of {@link BlockSelectorParser}.
     */
    public static final DecoratableArgument<BlockSelector> BLOCK_SELECTOR = new DecoratableArgument<>(new BlockSelectorParser());

    /**
     * The default instance of {@link TextParserToComponentParser}.
     */
    public static final DecoratableArgument<Component> MESSAGE = new DecoratableArgument<>(new TextParserToComponentParser(BetonQuest.getInstance().getTextParser()));

    /**
     * The default instance of {@link UUIDParser}.
     */
    public static final DecoratableArgument<UUID> UUID = new DecoratableArgument<>(new UUIDParser());

    /**
     * The default instance of {@link EnumParser}.
     *
     * @param <E>      the type of the enum
     * @param enumType the type of the enum
     * @return the parser for the enum
     */
    public static <E extends Enum<E>> DecoratedArgument<E> forEnumeration(final Class<E> enumType) {
        return new DecoratableArgument<>(new EnumParser<>(enumType));
    }

    @Override
    public <E extends Enum<E>> DecoratedArgument<E> forEnum(final Class<E> enumType) {
        return new DecoratableArgument<>(new EnumParser<>(enumType));
    }

    @Override
    public DecoratedArgument<String> string() {
        return STRING;
    }

    @Override
    public DecoratedArgument<Boolean> bool() {
        return BOOLEAN;
    }

    @Override
    public DecoratedArgument<Vector> vector() {
        return VECTOR;
    }

    @Override
    public DecoratedArgument<World> world() {
        return WORLD;
    }

    @Override
    public DecoratedArgument<Location> location() {
        return LOCATION;
    }

    @Override
    public DecoratedArgument<Component> component() {
        return MESSAGE;
    }

    @Override
    public DecoratedArgument<UUID> uuid() {
        return UUID;
    }

    @Override
    public DecoratedArgument<Number> number() {
        return NUMBER;
    }
}
