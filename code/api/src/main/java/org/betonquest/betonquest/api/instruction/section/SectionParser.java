package org.betonquest.betonquest.api.instruction.section;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
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
 * The middle step of the section instruction chain for key-value retrieval.
 * This class offers methods to decide on how to parse the argument.
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface SectionParser {

    /**
     * Use the given parser to parse the argument.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     */
    <T> DecoratableSectionRetriever<T> parse(InstructionArgumentParser<T> parser);

    /**
     * Use the given parser to parse the argument.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     */
    default <T> DecoratableSectionRetriever<T> parse(final SimpleArgumentParser<T> parser) {
        return parse((InstructionArgumentParser<T>) parser);
    }

    /**
     * Parses the subsection into a value of type T.
     *
     * @param sectionParser the parser to use for the subsection
     * @param <T>           the type of the parsed value
     * @return a new {@link DecoratableSectionRetriever} for the parsed value
     */
    <T> DecoratableSectionRetriever<T> section(SubSectionArgumentParser<T> sectionParser);

    /**
     * Use {@link ArgumentParsers#number()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the number parser
     */
    NumberSectionRetriever number();

    /**
     * Use {@link ArgumentParsers#string()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the string parser
     */
    DecoratableSectionRetriever<String> string();

    /**
     * Use {@link ArgumentParsers#bool()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the bool parser
     */
    DecoratableSectionRetriever<Boolean> bool();

    /**
     * Use {@link ArgumentParsers#item()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the item parser
     */
    DecoratableSectionRetriever<ItemWrapper> item();

    /**
     * Use {@link ArgumentParsers#vector()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the vector parser
     */
    DecoratableSectionRetriever<Vector> vector();

    /**
     * Use {@link ArgumentParsers#location()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the location parser
     */
    DecoratableSectionRetriever<Location> location();

    /**
     * Use {@link ArgumentParsers#namespacedKey()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the namespaced key parser
     */
    DecoratableSectionRetriever<NamespacedKey> namespacedKey();

    /**
     * Use {@link ArgumentParsers#component()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the component parser
     */
    DecoratableSectionRetriever<Component> component();

    /**
     * Use {@link ArgumentParsers#world()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the world parser
     */
    DecoratableSectionRetriever<World> world();

    /**
     * Use {@link ArgumentParsers#blockSelector()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the block selector parser
     */
    DecoratableSectionRetriever<BlockSelector> blockSelector();

    /**
     * Use {@link ArgumentParsers#packageIdentifier()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the package identifier parser
     */
    DecoratableSectionRetriever<String> packageIdentifier();

    /**
     * Use {@link ArgumentParsers#uuid()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the uuid parser
     */
    DecoratableSectionRetriever<UUID> uuid();

    /**
     * Use {@link ArgumentParsers#forEnum(Class)} to parse the argument.
     *
     * @param enumClass the enum class to parse
     * @param <E>       the enum type
     * @return a new {@link SectionRetriever} for the argument with the enum parser
     */
    <E extends Enum<E>> DecoratableSectionRetriever<E> enumeration(Class<E> enumClass);
}
