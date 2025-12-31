package org.betonquest.betonquest.lib.instruction.section.capsules;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SubSectionArgumentParser;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The wrapper for a {@link SubSectionArgumentParser} to be accessed
 * like a {@link InstructionArgumentParser} to create a list.
 *
 * @param <T> the type of the resolved value.
 */
public class EncapsulatedListSubSectionParser<T> implements InstructionArgumentParser<List<T>> {

    /**
     * The parent instruction.
     */
    private final SectionInstruction parentInstruction;

    /**
     * The parser to use.
     */
    private final SubSectionArgumentParser<T> parser;

    /**
     * Creates a new EncapsuledSectionParser.
     *
     * @param parentInstruction the parent instruction.
     * @param parser            the parser to use.
     */
    public EncapsulatedListSubSectionParser(final SectionInstruction parentInstruction, final SubSectionArgumentParser<T> parser) {
        this.parentInstruction = parentInstruction;
        this.parser = parser;
    }

    @Override
    public List<T> apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String path) throws QuestException {
        final List<Map<?, ?>> list = parentInstruction.getSection().getMapList(path);
        final List<T> values = new ArrayList<>(list.size());
        final YamlConfiguration tmp = new YamlConfiguration();
        for (int i = 0; i < list.size(); i++) {
            final Map<?, ?> value = list.get(i);
            final ConfigurationSection section = tmp.createSection("x" + i, value);
            final SectionInstruction entryInstruction = parentInstruction.cloneWithSection(section);
            values.add(parser.parse(entryInstruction));
        }
        return values;
    }
}
