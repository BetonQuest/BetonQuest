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
     * The default instance of {@link LocationParser}.
     */
    public final DecoratableArgument<Location> defaultLocationParser;

    /**
     * The default instance of {@link BooleanParser}.
     */
    public final DecoratableArgument<Boolean> defaultBooleanParser;

    /**
     * The default instance of {@link UUIDParser}.
     */
    public final DecoratableArgument<UUID> defaultUUIDParser;

    /**
     * The default instance of {@link TextParserToComponentParser}.
     */
    public final DecoratableArgument<Component> defaultComponentParser;

    /**
     * The default instance of {@link VectorParser}.
     */
    public final DecoratableArgument<Vector> defaultVectorParser;

    /**
     * The default instance of {@link WorldParser}.
     */
    public final DecoratableArgument<World> defaultWorldParser;

    /**
     * The default decoratable instance of {@link StringParser}.
     */
    private final DecoratableArgument<String> defaultStringParser;

    /**
     * Creates a new instance of {@link DefaultArgumentParsers}.
     */
    public DefaultArgumentParsers() {
        defaultStringParser = new DecoratableArgument<>(new StringParser());
        defaultBooleanParser = new DecoratableArgument<>(new BooleanParser());
        defaultUUIDParser = new DecoratableArgument<>(new UUIDParser());
        defaultComponentParser = new DecoratableArgument<>(new TextParserToComponentParser(BetonQuest.getInstance().getTextParser()));
        defaultVectorParser = new DecoratableArgument<>(new VectorParser());
        defaultWorldParser = new DecoratableArgument<>(new WorldParser(Bukkit.getServer()));
        defaultLocationParser = new DecoratableArgument<>(new LocationParser(Bukkit.getServer()));
    }

    @Override
    public <E extends Enum<E>> DecoratedArgument<E> forEnum(final Class<E> enumType) {
        return new DecoratableArgument<>(new EnumParser<>(enumType));
    }

    @Override
    public DecoratedArgument<String> string() {
        return defaultStringParser;
    }

    @Override
    public DecoratedArgument<Boolean> bool() {
        return defaultBooleanParser;
    }

    @Override
    public DecoratedArgument<Vector> vector() {
        return defaultVectorParser;
    }

    @Override
    public DecoratedArgument<World> world() {
        return defaultWorldParser;
    }

    @Override
    public DecoratedArgument<Location> location() {
        return defaultLocationParser;
    }

    @Override
    public DecoratedArgument<Component> component() {
        return defaultComponentParser;
    }

    @Override
    public DecoratedArgument<UUID> uuid() {
        return defaultUUIDParser;
    }

    @Override
    public DecoratedArgument<Number> number() {
        return NUMBER;
    }
}
