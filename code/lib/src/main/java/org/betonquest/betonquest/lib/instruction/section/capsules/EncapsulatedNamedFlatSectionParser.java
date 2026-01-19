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
import java.util.Map;
import java.util.Set;

/**
 * The wrapper for a {@link NamedSubSectionArgumentParser} to be accessed
 * like a {@link InstructionArgumentParser} to create a list.
 *
 * @param <T> the type of the resolved value.
 */
public class EncapsulatedNamedFlatSectionParser<T> implements InstructionArgumentParser<List<Map.Entry<String, T>>> {

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
    public EncapsulatedNamedFlatSectionParser(final SectionInstruction parentInstruction, final InstructionArgumentParser<T> parser) {
        this.parentInstruction = parentInstruction;
        this.parser = parser;
    }

    @Override
    public List<Map.Entry<String, T>> apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String path) throws QuestException {
        final ConfigurationSection sectionWithNamedFlatSections = parentInstruction.getSection().getConfigurationSection(path);
        if (sectionWithNamedFlatSections == null) {
            throw new QuestException("Section with named flat subsections not found: %s".formatted(path));
        }
        final Set<String> subSectionsKeys = sectionWithNamedFlatSections.getKeys(false);
        final List<Map.Entry<String, T>> values = new ArrayList<>(subSectionsKeys.size());
        for (final String subSectionKey : subSectionsKeys) {
            final String rawValue = sectionWithNamedFlatSections.getString(subSectionKey);
            values.add(Map.entry(subSectionKey, parser.apply(placeholders, packManager, pack, rawValue)));
        }
        return values;
    }
}
