package org.betonquest.betonquest.lib.instruction.section;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.ListSectionParser;
import org.betonquest.betonquest.api.instruction.section.ListSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.NamedSubSectionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.lib.instruction.section.capsules.EncapsulatedListSectionParser;
import org.betonquest.betonquest.lib.instruction.section.capsules.EncapsulatedListSubSectionParser;
import org.betonquest.betonquest.lib.instruction.section.capsules.EncapsulatedNamedFlatSectionParser;
import org.betonquest.betonquest.lib.instruction.section.capsules.EncapsulatedNamedSubSectionParser;
import org.betonquest.betonquest.lib.instruction.source.DefaultedSource;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The default implementation for {@link ListSectionParser}.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.TooManyMethods"})
public class DefaultListSectionParser implements ListSectionParser {

    /**
     * Used to enable path mode and pass down to the retriever.
     */
    private static final boolean CONFIG_SECTION_MODE = true;

    /**
     * The instruction used to retrieve the section.
     */
    private final SectionInstruction instruction;

    /**
     * The root path to the section.
     */
    private final ValueSource<List<String>> rootPath;

    /**
     * The parsers used to parse the section.
     */
    private final ArgumentParsers parsers;

    /**
     * Creates a new DefaultListSectionParser.
     *
     * @param instruction the instruction used to retrieve the section.
     * @param parsers     the parsers used to parse the section.
     * @param rootPath    the root path to the section.
     */
    public DefaultListSectionParser(final SectionInstruction instruction, final ArgumentParsers parsers, final ValueSource<List<String>> rootPath) {
        this.instruction = instruction;
        this.parsers = parsers;
        this.rootPath = rootPath;
    }

    private <T> ListSectionRetriever<T> listSection(final InstructionArgumentParser<T> parser, final boolean cache) {
        final EncapsulatedListSectionParser<T> listSectionParser = new EncapsulatedListSectionParser<>(instruction, parser);
        return new DefaultListSectionRetriever<>(instruction, rootPath, listSectionParser, CONFIG_SECTION_MODE, cache);
    }

    @Override
    public ListSectionParser fallback(final ValueSource<List<String>> fallbackSource) {
        return new DefaultListSectionParser(instruction, parsers, new DefaultedSource<>(rootPath, fallbackSource));
    }

    @Override
    public <T> ListSectionRetriever<T> parse(final InstructionArgumentParser<T> parser) {
        return listSection(parser, false);
    }

    @Override
    public <T> ListSectionRetriever<T> section(final SubSectionArgumentParser<T> sectionParser) {
        final EncapsulatedListSubSectionParser<T> subSectionParser = new EncapsulatedListSubSectionParser<>(instruction, sectionParser);
        return new DefaultListSectionRetriever<>(instruction, rootPath, subSectionParser, CONFIG_SECTION_MODE, false);
    }

    @Override
    public <T> ListSectionRetriever<T> namedSections(final NamedSubSectionArgumentParser<T> sectionParser) {
        final EncapsulatedNamedSubSectionParser<T> namedSectionParser = new EncapsulatedNamedSubSectionParser<>(instruction, sectionParser);
        return new DefaultListSectionRetriever<>(instruction, rootPath, namedSectionParser, CONFIG_SECTION_MODE, false);
    }

    @Override
    public <T> ListSectionRetriever<Map.Entry<String, T>> namedValues(final InstructionArgumentParser<T> parser) {
        final EncapsulatedNamedFlatSectionParser<T> namedSectionParser = new EncapsulatedNamedFlatSectionParser<>(instruction, parser);
        return new DefaultListSectionRetriever<>(instruction, rootPath, namedSectionParser, CONFIG_SECTION_MODE, false);
    }

    @Override
    public ListSectionRetriever<Map.Entry<String, String>> namedStrings() {
        return namedValues(parsers.string());
    }

    @Override
    public ListSectionRetriever<String> string() {
        return listSection(parsers.string(), true);
    }

    @Override
    public ListSectionRetriever<Number> number() {
        return listSection(parsers.number(), true);
    }

    @Override
    public ListSectionRetriever<ItemWrapper> item() {
        return listSection(parsers.item(), true);
    }

    @Override
    public ListSectionRetriever<Vector> vector() {
        return listSection(parsers.vector(), false);
    }

    @Override
    public ListSectionRetriever<Location> location() {
        return listSection(parsers.location(), false);
    }

    @Override
    public ListSectionRetriever<World> world() {
        return listSection(parsers.world(), false);
    }

    @Override
    public ListSectionRetriever<NamespacedKey> namespacedKey() {
        return listSection(parsers.namespacedKey(), true);
    }

    @Override
    public ListSectionRetriever<Component> component() {
        return listSection(parsers.component(), true);
    }

    @Override
    public ListSectionRetriever<BlockSelector> blockSelector() {
        return listSection(parsers.blockSelector(), true);
    }

    @Override
    public ListSectionRetriever<String> packageIdentifier() {
        return listSection(parsers.packageIdentifier(), true);
    }

    @Override
    public ListSectionRetriever<UUID> uuid() {
        return listSection(parsers.uuid(), true);
    }

    @Override
    public <E extends Enum<E>> ListSectionRetriever<E> enumeration(final Class<E> enumClass) {
        return listSection(parsers.forEnum(enumClass), true);
    }
}
