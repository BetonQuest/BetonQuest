package org.betonquest.betonquest.api.instruction.argument.parser;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.NumberArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.betonquest.betonquest.lib.instruction.argument.DefaultNumberArgumentParser;
import org.betonquest.betonquest.text.ParsedSectionTextCreator;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * This offers default implementations for {@link DecoratedArgumentParser} to parse common types.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultArgumentParsers implements ArgumentParsers {

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
     * The default decoratable instance of {@link PackageIdentifierParser}.
     */
    private final DecoratedArgumentParser<String> defaultPackageIdentifier;

    /**
     * The default decoratable instance of {@link NamespacedKeyParser}.
     */
    private final DecoratedArgumentParser<NamespacedKey> defaultNamespacedKeyParser;

    /**
     * The default instance of {@link TranslationSectionParser}.
     */
    private final SubSectionArgumentParser<Text> defaultTranslationSectionParser;

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
     * The identifier registry to get identifier factories from.
     */
    private final Identifiers registry;

    /**
     * Creates a new instance of {@link DefaultArgumentParsers}
     * and all default instances of {@link DecoratedArgumentParser}s.
     *
     * @param itemManager           the item manager to use for item parsing
     * @param itemIdentifierFactory the identifier factory to use for item parsing
     * @param textParser            the text parser to use for component parsing
     * @param server                the server to use for world and location parsing
     * @param registry              the identifier registry to get identifier factories from
     * @param textCreator           the text creator to use for translation section parsing
     */
    public DefaultArgumentParsers(final ItemManager itemManager, final IdentifierFactory<ItemIdentifier> itemIdentifierFactory,
                                  final TextParser textParser, final Server server, final Identifiers registry,
                                  final ParsedSectionTextCreator textCreator) {
        this.registry = registry;
        defaultBlockSelectorParser = new DecoratableArgumentParser<>(new BlockSelectorParser());
        defaultComponentParser = new DecoratableArgumentParser<>(new TextParserToComponentParser(textParser));
        defaultNumberParser = new DefaultNumberArgumentParser(new NumberParser());
        defaultLocationParser = new DecoratableArgumentParser<>(new LocationParser(server));
        defaultItemParser = new DecoratableArgumentParser<>(new ItemParser(itemManager, itemIdentifierFactory));
        defaultPackageIdentifier = new DecoratableArgumentParser<>(new PackageIdentifierParser());
        defaultNamespacedKeyParser = new DecoratableArgumentParser<>(new NamespacedKeyParser());
        defaultBooleanParser = new DecoratableArgumentParser<>(new BooleanParser());
        defaultUUIDParser = new DecoratableArgumentParser<>(new UUIDParser());
        defaultVectorParser = new DecoratableArgumentParser<>(new VectorParser());
        defaultWorldParser = new DecoratableArgumentParser<>(new WorldParser(server));
        defaultStringParser = new DecoratableArgumentParser<>(new StringParser());
        defaultTranslationSectionParser = new TranslationSectionParser(textCreator);
    }

    @Override
    public <E extends Enum<E>> DecoratedArgumentParser<E> forEnum(final Class<E> enumType) {
        return new DecoratableArgumentParser<>(new EnumParser<>(enumType));
    }

    @Override
    public <I extends Identifier> DecoratedArgumentParser<I> forIdentifier(final Class<I> identifierType) {
        return new DecoratableArgumentParser<>(new IdentifierParser<>(registry, identifierType));
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
    public SubSectionArgumentParser<Text> translationSection() {
        return defaultTranslationSectionParser;
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
