package org.betonquest.betonquest.lib.instruction.section;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.DecoratableSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.NumberSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionParser;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.lib.instruction.section.capsules.EncapsulatedSectionParser;
import org.betonquest.betonquest.lib.instruction.source.DefaultedSource;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

/**
 * The default implementation for {@link SectionParser}.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.CouplingBetweenObjects"})
public class DefaultSectionParser implements SectionParser {

    /**
     * Used to disable path mode and pass down to the retriever.
     */
    private static final boolean SINGLE_VALUE_MODE = false;

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
     * Creates a new DefaultSectionParser.
     *
     * @param instruction the instruction used to retrieve the section.
     * @param parsers     the parsers used to parse the section.
     * @param rootPath    the root path to the section.
     */
    public DefaultSectionParser(final SectionInstruction instruction, final ArgumentParsers parsers,
                                final ValueSource<List<String>> rootPath) {
        this.instruction = instruction;
        this.parsers = parsers;
        this.rootPath = rootPath;
    }

    @Override
    public SectionParser fallback(final ValueSource<List<String>> fallbackSource) {
        return new DefaultSectionParser(instruction, parsers, new DefaultedSource<>(rootPath, fallbackSource));
    }

    @Override
    public <T> DecoratableSectionRetriever<T> parse(final InstructionArgumentParser<T> parser) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parser, SINGLE_VALUE_MODE);
    }

    @Override
    public <T> DecoratableSectionRetriever<T> section(final SubSectionArgumentParser<T> sectionParser) {
        final EncapsulatedSectionParser<T> encapsulated = new EncapsulatedSectionParser<>(instruction, sectionParser);
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, encapsulated, CONFIG_SECTION_MODE);
    }

    @Override
    public NumberSectionRetriever number() {
        return new DefaultNumberSectionRetriever(instruction, rootPath, parsers.number(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<String> string() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.string(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<Boolean> bool() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.bool(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<ItemWrapper> item() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.item(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<Vector> vector() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.vector(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<Location> location() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.location(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<NamespacedKey> namespacedKey() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.namespacedKey(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<Component> component() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.component(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<World> world() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.world(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<BlockSelector> blockSelector() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.blockSelector(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<String> packageIdentifier() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.packageIdentifier(), SINGLE_VALUE_MODE);
    }

    @Override
    public DecoratableSectionRetriever<UUID> uuid() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.uuid(), SINGLE_VALUE_MODE);
    }

    @Override
    public <E extends Enum<E>> DecoratableSectionRetriever<E> enumeration(final Class<E> enumClass) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.forEnum(enumClass), SINGLE_VALUE_MODE);
    }
}
