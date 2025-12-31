package org.betonquest.betonquest.lib.instruction.section.capsules;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.NamedSubSectionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The wrapper for a {@link NamedSubSectionArgumentParser} to be accessed
 * like a {@link InstructionArgumentParser} to create a list.
 *
 * @param <T> the type of the resolved value.
 */
public class EncapsulatedNamedSubSectionParser<T> implements InstructionArgumentParser<List<T>> {

    /**
     * The parent instruction.
     */
    private final SectionInstruction parentInstruction;

    /**
     * The parser to use.
     */
    private final NamedSubSectionArgumentParser<T> parser;

    /**
     * Creates a new EncapsuledSectionParser.
     *
     * @param parentInstruction the parent instruction.
     * @param parser            the parser to use.
     */
    public EncapsulatedNamedSubSectionParser(final SectionInstruction parentInstruction, final NamedSubSectionArgumentParser<T> parser) {
        this.parentInstruction = parentInstruction;
        this.parser = parser;
    }

    @Override
    public List<T> apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String path) throws QuestException {
        final ConfigurationSection sectionWithNamedSubSections = parentInstruction.getSection().getConfigurationSection(path);
        if (sectionWithNamedSubSections == null) {
            throw new QuestException("Section not found: " + path);
        }
        final Set<String> subSectionsKeys = sectionWithNamedSubSections.getKeys(false);
        final List<T> values = new ArrayList<>(subSectionsKeys.size());
        for (final String subSectionKey : subSectionsKeys) {
            final SectionInstruction entryInstruction = parentInstruction.subSection(path, subSectionKey);
            values.add(parser.parse(subSectionKey, entryInstruction));
        }
        return values;
    }
}
