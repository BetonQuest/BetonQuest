package org.betonquest.betonquest.api.instruction.section;

import net.kyori.adventure.text.Component;
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
 * The middle step of the section instruction chain for key-list retrieval.
 * This class offers methods to decide on how to parse the elements of the list.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface ListSectionParser {

    /**
     * Parses the element list into a list of values of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     */
    <T> ListSectionRetriever<T> parse(InstructionArgumentParser<T> parser);

    /**
     * Parses the element list into a list of values of type T using the given parser.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     */
    default <T> ListSectionRetriever<T> parse(final SimpleArgumentParser<T> parser) {
        return parse((InstructionArgumentParser<T>) parser);
    }

    /**
     * Parses the subsection into a value of type T.
     *
     * @param parser the parser to use
     * @param <T>    the type of the parsed value
     * @return a new {@link DecoratableSectionRetriever} for the parsed value
     */
    <T> ListSectionRetriever<T> section(SubSectionArgumentParser<T> parser);

    /**
     * Use the given parser to parse a number of named subsections into a list of values of T.
     *
     * @param sectionParser the parser to use for each of the subsections
     * @param <T>           the type of the parsed values
     * @return a new {@link ListSectionRetriever} for the parsed values
     */
    <T> ListSectionRetriever<T> namedSections(NamedSubSectionArgumentParser<T> sectionParser);

    /**
     * Parses the element list into a list of strings.
     *
     * @return a new {@link ListSectionRetriever} for strings
     */
    ListSectionRetriever<String> string();

    /**
     * Parses the element list into a list of numbers.
     *
     * @return a new {@link ListSectionRetriever} for numbers
     */
    ListSectionRetriever<Number> number();

    /**
     * Parses the element list into a list of items.
     *
     * @return a new {@link ListSectionRetriever} for items
     */
    ListSectionRetriever<ItemWrapper> item();

    /**
     * Parses the element list into a list of vectors.
     *
     * @return a new {@link ListSectionRetriever} for vectors
     */
    ListSectionRetriever<Vector> vector();

    /**
     * Parses the element list into a list of locations.
     *
     * @return a new {@link ListSectionRetriever} for locations
     */
    ListSectionRetriever<Location> location();

    /**
     * Parses the element list into a list of namespaced keys.
     *
     * @return a new {@link ListSectionRetriever} for namespaced keys
     */
    ListSectionRetriever<NamespacedKey> namespacedKey();

    /**
     * Parses the element list into a list of components.
     *
     * @return a new {@link ListSectionRetriever} for components
     */
    ListSectionRetriever<Component> component();

    /**
     * Parses the element list into a list of worlds.
     *
     * @return a new {@link ListSectionRetriever} for worlds
     */
    ListSectionRetriever<World> world();

    /**
     * Parses the element list into a list of block selectors.
     *
     * @return a new {@link ListSectionRetriever} for block selectors
     */
    ListSectionRetriever<BlockSelector> blockSelector();

    /**
     * Parses the element list into a list of package identifiers.
     *
     * @return a new {@link ListSectionRetriever} for package identifiers
     */
    ListSectionRetriever<String> packageIdentifier();

    /**
     * Parses the element list into a list of UUIDs.
     *
     * @return a new {@link ListSectionRetriever} for UUIDs
     */
    ListSectionRetriever<UUID> uuid();

    /**
     * Parses the element list into a list of enumerations.
     *
     * @param enumClass the type of the enum
     * @param <E>       the enum type
     * @return a new {@link ListSectionRetriever} for enumerations
     */
    <E extends Enum<E>> ListSectionRetriever<E> enumeration(Class<E> enumClass);
}
