package org.betonquest.betonquest.api.instruction.section;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The middle step of the section instruction chain for key-list retrieval.
 * This class offers methods to decide on how to parse the elements of the list.
 *
 * @since 3.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ListSectionParser {

    /**
     * Adds a fallback source for the section.
     * All further fallbacks will be added in order with lower priority.
     *
     * @param fallbackSource the fallback source to add
     * @return this instance for chaining
     * @since 3.0.0
     */
    ListSectionParser fallback(ValueSource<List<String>> fallbackSource);

    /**
     * Adds a fallback path for the section.
     * All further fallbacks will be added in order with lower priority.
     * <br>
     * Utilizes {@link #fallback(ValueSource)} internally by default.
     *
     * @param fallbackPath the fallback path to add
     * @return this instance for chaining
     * @since 3.0.0
     */
    default ListSectionParser fallback(final String... fallbackPath) {
        return fallback(() -> List.of(fallbackPath));
    }

    /**
     * Parses the element list into a list of values of type T using the given parser.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     * @since 3.0.0
     */
    <T> ListSectionRetriever<T> parse(InstructionArgumentParser<T> parser);

    /**
     * Parses the element list into a list of values of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     * @since 3.0.0
     */
    default <T> ListSectionRetriever<T> parse(final SimpleArgumentParser<T> parser) {
        return parse((InstructionArgumentParser<T>) parser);
    }

    /**
     * Parses the subsection into a value of type T.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed value
     * @return a new {@link DecoratableSectionRetriever} for the parsed value
     * @since 3.0.0
     */
    <T> ListSectionRetriever<T> section(SubSectionArgumentParser<T> parser);

    /**
     * Use the given parser to parse a number of named subsections into a list of values of T.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @param sectionParser the parser to use for each of the subsections
     * @param <T>           the type of the parsed values
     * @return a new {@link ListSectionRetriever} for the parsed values
     * @since 3.0.0
     */
    <T> ListSectionRetriever<T> namedSections(NamedSubSectionArgumentParser<T> sectionParser);

    /**
     * Use the given parser to parse a number of named key-value sections into a list of values of T.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @param sectionParser the parser to use for each of the sections
     * @param <T>           the type of the parsed values
     * @return a new {@link ListSectionRetriever} for the parsed values
     * @since 3.0.0
     */
    <T> ListSectionRetriever<Map.Entry<String, T>> namedValues(InstructionArgumentParser<T> sectionParser);

    /**
     * Use the given parser to parse a number of named key-value sections into a list of values of T.
     *
     * @param sectionParser the parser to use for each of the sections
     * @param <T>           the type of the parsed values
     * @return a new {@link ListSectionRetriever} for the parsed values
     * @since 3.0.0
     */
    default <T> ListSectionRetriever<Map.Entry<String, T>> namedValues(final SimpleArgumentParser<T> sectionParser) {
        return namedValues((InstructionArgumentParser<T>) sectionParser);
    }

    /**
     * Use the given parser to parse a number of named key-value sections into a list of strings.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for the parsed values
     * @since 3.0.0
     */
    ListSectionRetriever<Map.Entry<String, String>> namedStrings();

    /**
     * Parses the element list into a list of strings.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for strings
     * @since 3.0.0
     */
    ListSectionRetriever<String> string();

    /**
     * Parses the element list into a list of numbers.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for numbers
     * @since 3.0.0
     */
    ListSectionRetriever<Number> number();

    /**
     * Parses the element list into a list of items.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for items
     * @since 3.0.0
     */
    ListSectionRetriever<ItemWrapper> item();

    /**
     * Parses the element list into a list of vectors.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link ListSectionRetriever} for vectors
     * @since 3.0.0
     */
    ListSectionRetriever<Vector> vector();

    /**
     * Parses the element list into a list of locations.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link ListSectionRetriever} for locations
     * @since 3.0.0
     */
    ListSectionRetriever<Location> location();

    /**
     * Parses the element list into a list of worlds.
     * <p>
     * <b>World objects should never be stored persistent to prevent memory leaks when they are unloaded!</b>
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is disabled by default.
     *
     * @return a new {@link ListSectionRetriever} for worlds
     * @since 3.0.0
     */
    ListSectionRetriever<World> world();

    /**
     * Parses the element list into a list of namespaced keys.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for namespaced keys
     * @since 3.0.0
     */
    ListSectionRetriever<NamespacedKey> namespacedKey();

    /**
     * Parses the element list into a list of components.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for components
     * @since 3.0.0
     */
    ListSectionRetriever<Component> component();

    /**
     * Parses the element list into a list of block selectors.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for block selectors
     * @since 3.0.0
     */
    ListSectionRetriever<BlockSelector> blockSelector();

    /**
     * Parses the element list into a list of package identifiers.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for package identifiers
     * @since 3.0.0
     */
    ListSectionRetriever<String> packageIdentifier();

    /**
     * Parses the element list into a list of UUIDs.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @return a new {@link ListSectionRetriever} for UUIDs
     * @since 3.0.0
     */
    ListSectionRetriever<UUID> uuid();

    /**
     * Parses the element list into a list of enumerations.
     * <p>
     * The {@link DecoratableSectionRetriever#cache(boolean) caching} is enabled by default.
     *
     * @param enumClass the type of the enum
     * @param <E>       the enum type
     * @return a new {@link ListSectionRetriever} for enumerations
     * @since 3.0.0
     */
    <E extends Enum<E>> ListSectionRetriever<E> enumeration(Class<E> enumClass);
}
