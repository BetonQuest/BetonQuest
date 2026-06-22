package org.betonquest.betonquest.api.instruction.section;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.text.Text;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

/**
 * The middle step of the section instruction chain for key-value retrieval.
 * This class offers methods to decide on how to parse the argument.
 *
 * @since 3.0.0
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface SectionParser {

    /**
     * Adds a fallback source for the section.
     * All further fallbacks will be added in order with lower priority.
     *
     * @param fallbackSource the fallback source to add
     * @return this instance for chaining
     * @since 3.0.0
     */
    SectionParser fallback(ValueSource<List<String>> fallbackSource);

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
    default SectionParser fallback(final String... fallbackPath) {
        return fallback(() -> List.of(fallbackPath));
    }

    /**
     * Use the given parser to parse the argument.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     * @since 3.0.0
     */
    <T> DecoratableSectionRetriever<T> parse(InstructionArgumentParser<T> parser);

    /**
     * Use the given parser to parse the argument.
     *
     * @param parser the parser to use
     * @param <T>    the type of the argument
     * @return a new {@link SectionRetriever} for the argument with the given parser
     * @since 3.0.0
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
     * @since 3.0.0
     */
    <T> DecoratableSectionRetriever<T> section(SubSectionArgumentParser<T> sectionParser);

    /**
     * Use {@link ArgumentParsers#number()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the number parser
     * @since 3.0.0
     */
    NumberSectionRetriever number();

    /**
     * Use {@link ArgumentParsers#string()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the string parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<String> string();

    /**
     * Use {@link ArgumentParsers#bool()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the bool parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<Boolean> bool();

    /**
     * Use {@link ArgumentParsers#item()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the item parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<ItemWrapper> item();

    /**
     * Use {@link ArgumentParsers#vector()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the vector parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<Vector> vector();

    /**
     * Use {@link ArgumentParsers#location()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the location parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<Location> location();

    /**
     * Use {@link ArgumentParsers#namespacedKey()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the namespaced key parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<NamespacedKey> namespacedKey();

    /**
     * Use {@link ArgumentParsers#translationSection()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the translation section parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<Text> translationSection();

    /**
     * Use {@link ArgumentParsers#component()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the component parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<Component> component();

    /**
     * Use {@link ArgumentParsers#world()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the world parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<World> world();

    /**
     * Use {@link ArgumentParsers#blockSelector()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the block selector parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<BlockSelector> blockSelector();

    /**
     * Use {@link ArgumentParsers#packageIdentifier()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the package identifier parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<String> packageIdentifier();

    /**
     * Use {@link ArgumentParsers#uuid()} to parse the argument.
     *
     * @return a new {@link SectionRetriever} for the argument with the uuid parser
     * @since 3.0.0
     */
    DecoratableSectionRetriever<UUID> uuid();

    /**
     * Use {@link ArgumentParsers#forEnum(Class)} to parse the argument.
     *
     * @param enumClass the enum class to parse
     * @param <E>       the enum type
     * @return a new {@link SectionRetriever} for the argument with the enum parser
     * @since 3.0.0
     */
    <E extends Enum<E>> DecoratableSectionRetriever<E> enumeration(Class<E> enumClass);

    /**
     * Use {@link ArgumentParsers#forIdentifier(Class)} to parse the argument.
     *
     * @param identifierClass the identifier class to parse
     * @param <I>             the identifier type
     * @return a new {@link SectionRetriever} for the argument with the identifier parser
     * @since 3.0.0
     */
    <I extends Identifier> DecoratableSectionRetriever<I> identifier(Class<I> identifierClass);
}
