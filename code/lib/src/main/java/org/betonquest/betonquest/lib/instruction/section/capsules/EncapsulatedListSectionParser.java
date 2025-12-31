package org.betonquest.betonquest.lib.instruction.section.capsules;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.quest.Placeholders;

import java.util.ArrayList;
import java.util.List;

/**
 * The wrapper for a {@link SubSectionArgumentParser} to be accessed like a {@link SimpleArgumentParser}.
 *
 * @param <T> the type of the resolved value.
 */
public class EncapsulatedListSectionParser<T> implements InstructionArgumentParser<List<T>> {

    /**
     * The parent instruction.
     */
    private final SectionInstruction parentInstruction;

    /**
     * The parser to use.
     */
    private final InstructionArgumentParser<T> parser;

    /**
     * Creates a new EncapsuledSectionParser.
     *
     * @param parentInstruction the parent instruction.
     * @param parser            the parser to use.
     */
    public EncapsulatedListSectionParser(final SectionInstruction parentInstruction, final InstructionArgumentParser<T> parser) {
        this.parentInstruction = parentInstruction;
        this.parser = parser;
    }

    @Override
    public List<T> apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String path) throws QuestException {
        final List<String> list = parentInstruction.getSection().getStringList(path);
        final List<T> values = new ArrayList<>(list.size());
        for (final String value : list) {
            values.add(parser.apply(placeholders, packManager, pack, value));
        }
        return values;
    }
}
