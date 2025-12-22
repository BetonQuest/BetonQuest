package org.betonquest.betonquest.api.instruction.argument.parser;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratableArgument;
import org.betonquest.betonquest.api.instruction.argument.DecoratableNumberArgument;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgument;
import org.betonquest.betonquest.api.instruction.argument.DecoratedNumberArgument;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * This offers default implementations for {@link DecoratedArgument} to parse common types.
 */
public class DefaultArgumentParsers implements ArgumentParsers {

    /**
     * The default instance of {@link DefaultArgumentParsers}.
     */
    public static final DefaultArgumentParsers INSTANCE = new DefaultArgumentParsers();

    /**
     * The default decoratable instance of {@link BlockSelectorParser}.
     */
    public static final DecoratableArgument<BlockSelector> BLOCK_SELECTOR = new DecoratableArgument<>(new BlockSelectorParser());

    /**
     * The default instance of {@link TextParserToComponentParser}.
     */
    private final Argument<Component> defaultComponentParser;

    /**
     * The default decoratable instance of {@link NumberParser}.
     */
    private final DecoratedNumberArgument defaultNumberParser;

    /**
     * The default decoratable instance of {@link LocationParser}.
     */
    private final DecoratableArgument<Location> defaultLocationParser;

    /**
     * The default decoratable instance of {@link BooleanParser}.
     */
    private final DecoratableArgument<Boolean> defaultBooleanParser;

    /**
     * The default decoratable instance of {@link UUIDParser}.
     */
    private final DecoratableArgument<UUID> defaultUUIDParser;

    /**
     * The default decoratable instance of {@link VectorParser}.
     */
    private final DecoratableArgument<Vector> defaultVectorParser;

    /**
     * The default decoratable instance of {@link WorldParser}.
     */
    private final DecoratableArgument<World> defaultWorldParser;

    /**
     * The default decoratable instance of {@link StringParser}.
     */
    private final DecoratableArgument<String> defaultStringParser;

    /**
     * Creates a new instance of {@link DefaultArgumentParsers}
     * and all default instances of {@link DecoratedArgument}s.
     */
    public DefaultArgumentParsers() {
        defaultComponentParser = new TextParserToComponentParser(BetonQuest.getInstance().getTextParser());
        defaultNumberParser = new DecoratableNumberArgument(new NumberParser());
        defaultLocationParser = new DecoratableArgument<>(new LocationParser(Bukkit.getServer()));
        defaultBooleanParser = new DecoratableArgument<>(new BooleanParser());
        defaultUUIDParser = new DecoratableArgument<>(new UUIDParser());
        defaultVectorParser = new DecoratableArgument<>(new VectorParser());
        defaultWorldParser = new DecoratableArgument<>(new WorldParser(Bukkit.getServer()));
        defaultStringParser = new DecoratableArgument<>(new StringParser());
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
        return new DecoratableArgument<>(defaultComponentParser);
    }

    @Override
    public DecoratedArgument<UUID> uuid() {
        return defaultUUIDParser;
    }

    @Override
    public DecoratedNumberArgument number() {
        return defaultNumberParser;
    }
}
