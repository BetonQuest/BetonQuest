package org.betonquest.betonquest.api.instruction.argument.parser;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.betonquest.betonquest.lib.instruction.argument.DefaultNumberArgumentParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * This offers default implementations for {@link DecoratedArgumentParser} to parse common types.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultArgumentParsers implements ArgumentParsers {

    /**
     * The default instance of {@link DefaultArgumentParsers}.
     */
    public static final DefaultArgumentParsers INSTANCE = new DefaultArgumentParsers();

    /**
     * The default decoratable instance of {@link BlockSelectorParser}.
     */
    private final DecoratedArgumentParser<BlockSelector> defaultBlockSelectorParser;

    /**
     * The default instance of {@link TextParserToComponentParser}.
     */
    private final DecoratedArgumentParser<Component> defaultComponentParser;

    /**
     * The default decoratable instance of {@link NumberParser}.
     */
    private final NumberArgumentParser defaultNumberParser;

    /**
     * The default decoratable instance of {@link LocationParser}.
     */
    private final DecoratedArgumentParser<Location> defaultLocationParser;

    /**
     * The default decoratable instance of {@link ItemParser}.
     */
    private final DecoratedArgumentParser<ItemWrapper> defaultItemParser;

    /**
     * The default decoratable instance of {@link IdentifierParser}.
     */
    private final DecoratedArgumentParser<String> defaultPackageIdentifier;

    /**
     * The default decoratable instance of {@link NamespacedKeyParser}.
     */
    private final DecoratedArgumentParser<NamespacedKey> defaultNamespacedKeyParser;

    /**
     * The default decoratable instance of {@link BooleanParser}.
     */
    private final DecoratedArgumentParser<Boolean> defaultBooleanParser;

    /**
     * The default decoratable instance of {@link UUIDParser}.
     */
    private final DecoratedArgumentParser<UUID> defaultUUIDParser;

    /**
     * The default decoratable instance of {@link VectorParser}.
     */
    private final DecoratedArgumentParser<Vector> defaultVectorParser;

    /**
     * The default decoratable instance of {@link WorldParser}.
     */
    private final DecoratedArgumentParser<World> defaultWorldParser;

    /**
     * The default decoratable instance of {@link StringParser}.
     */
    private final DecoratedArgumentParser<String> defaultStringParser;

    /**
     * Creates a new instance of {@link DefaultArgumentParsers}
     * and all default instances of {@link DecoratedArgumentParser}s.
     */
    public DefaultArgumentParsers() {
        defaultBlockSelectorParser = new DecoratableArgumentParser<>(new BlockSelectorParser());
        defaultComponentParser = new DecoratableArgumentParser<>(new TextParserToComponentParser(BetonQuest.getInstance().getTextParser()));
        defaultNumberParser = new DefaultNumberArgumentParser(new NumberParser());
        defaultLocationParser = new DecoratableArgumentParser<>(new LocationParser(Bukkit.getServer()));
        defaultItemParser = new DecoratableArgumentParser<>(new ItemParser(BetonQuest.getInstance().getFeatureApi()));
        defaultPackageIdentifier = new DecoratableArgumentParser<>(new IdentifierParser());
        defaultNamespacedKeyParser = new DecoratableArgumentParser<>(new NamespacedKeyParser());
        defaultBooleanParser = new DecoratableArgumentParser<>(new BooleanParser());
        defaultUUIDParser = new DecoratableArgumentParser<>(new UUIDParser());
        defaultVectorParser = new DecoratableArgumentParser<>(new VectorParser());
        defaultWorldParser = new DecoratableArgumentParser<>(new WorldParser(Bukkit.getServer()));
        defaultStringParser = new DecoratableArgumentParser<>(new StringParser());
    }

    @Override
    public <E extends Enum<E>> DecoratedArgumentParser<E> forEnum(final Class<E> enumType) {
        return new DecoratableArgumentParser<>(new EnumParser<>(enumType));
    }

    @Override
    public DecoratedArgumentParser<String> string() {
        return defaultStringParser;
    }

    @Override
    public DecoratedArgumentParser<Boolean> bool() {
        return defaultBooleanParser;
    }

    @Override
    public DecoratedArgumentParser<Vector> vector() {
        return defaultVectorParser;
    }

    @Override
    public DecoratedArgumentParser<World> world() {
        return defaultWorldParser;
    }

    @Override
    public DecoratedArgumentParser<Location> location() {
        return defaultLocationParser;
    }

    @Override
    public DecoratedArgumentParser<ItemWrapper> item() {
        return defaultItemParser;
    }

    @Override
    public DecoratedArgumentParser<BlockSelector> blockSelector() {
        return defaultBlockSelectorParser;
    }

    @Override
    public DecoratedArgumentParser<String> packageIdentifier() {
        return defaultPackageIdentifier;
    }

    @Override
    public DecoratedArgumentParser<NamespacedKey> namespacedKey() {
        return defaultNamespacedKeyParser;
    }

    @Override
    public DecoratedArgumentParser<Component> component() {
        return defaultComponentParser;
    }

    @Override
    public DecoratedArgumentParser<UUID> uuid() {
        return defaultUUIDParser;
    }

    @Override
    public NumberArgumentParser number() {
        return defaultNumberParser;
    }
}
