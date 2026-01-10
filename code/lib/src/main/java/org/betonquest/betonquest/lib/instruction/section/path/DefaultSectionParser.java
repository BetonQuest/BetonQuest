package org.betonquest.betonquest.lib.instruction.section.path;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionChainInstruction;
import org.betonquest.betonquest.api.instruction.section.path.DecoratableSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.path.NumberSectionRetriever;
import org.betonquest.betonquest.api.instruction.section.path.SectionParser;
import org.betonquest.betonquest.api.instruction.section.path.SectionRetriever;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.UUID;

/**
 * The default implementation for {@link SectionParser}.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DefaultSectionParser implements SectionParser {

    /**
     * The instruction used to retrieve the section.
     */
    private final SectionChainInstruction instruction;

    /**
     * The root path to the section.
     */
    private final String rootPath;

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
    public DefaultSectionParser(final SectionChainInstruction instruction, final ArgumentParsers parsers, final String rootPath) {
        this.instruction = instruction;
        this.parsers = parsers;
        this.rootPath = rootPath;
    }

    @Override
    public <T> SectionRetriever<T> parse(final InstructionArgumentParser<T> parser) {
        return new DefaultSectionRetriever<>(instruction, rootPath, parser);
    }

    @Override
    public NumberSectionRetriever number() {
        return new DefaultNumberSectionRetriever(instruction, rootPath, parsers.number());
    }

    @Override
    public DecoratableSectionRetriever<String> string() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.string());
    }

    @Override
    public DecoratableSectionRetriever<Boolean> bool() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.bool());
    }

    @Override
    public DecoratableSectionRetriever<ItemWrapper> item() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.item());
    }

    @Override
    public DecoratableSectionRetriever<Vector> vector() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.vector());
    }

    @Override
    public DecoratableSectionRetriever<Location> location() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.location());
    }

    @Override
    public DecoratableSectionRetriever<NamespacedKey> namespacedKey() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.namespacedKey());
    }

    @Override
    public DecoratableSectionRetriever<Component> component() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.component());
    }

    @Override
    public DecoratableSectionRetriever<World> world() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.world());
    }

    @Override
    public DecoratableSectionRetriever<BlockSelector> blockSelector() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.blockSelector());
    }

    @Override
    public DecoratableSectionRetriever<String> packageIdentifier() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.packageIdentifier());
    }

    @Override
    public DecoratableSectionRetriever<UUID> uuid() {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.uuid());
    }

    @Override
    public <E extends Enum<E>> DecoratableSectionRetriever<E> enumeration(final Class<E> enumClass) {
        return new DefaultDecoratableSectionRetriever<>(instruction, rootPath, parsers.forEnum(enumClass));
    }
}
