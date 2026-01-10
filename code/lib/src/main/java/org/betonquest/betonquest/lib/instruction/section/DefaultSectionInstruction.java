package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionTraverser;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Default implementation of {@link SectionInstruction}.
 */
public class DefaultSectionInstruction implements SectionInstruction {

    /**
     * The placeholder provider used to resolve placeholders in the section.
     */
    private final Placeholders variables;

    /**
     * The parsers used to parse the section.
     */
    private final ArgumentParsers parsers;

    /**
     * The package manager used to resolve relative paths.
     */
    private final QuestPackageManager packageManager;

    /**
     * The quest package this instruction belongs to.
     */
    private final QuestPackage questPackage;

    /**
     * The section to parse.
     */
    private final ConfigurationSection section;

    /**
     * Creates a new DefaultSectionInstruction.
     *
     * @param parsers        the parsers used to parse the section
     * @param variables      the placeholder provider used to resolve placeholders in the section
     * @param packageManager the package manager used to resolve relative paths
     * @param questPackage   the quest package this instruction belongs to
     * @param section        the section to parse
     */
    public DefaultSectionInstruction(final ArgumentParsers parsers, final Placeholders variables,
                                     final QuestPackageManager packageManager, final QuestPackage questPackage,
                                     final ConfigurationSection section) {
        this.parsers = parsers;
        this.variables = variables;
        this.packageManager = packageManager;
        this.questPackage = questPackage;
        this.section = section;
    }

    @Override
    public ArgumentParsers getParsers() {
        return parsers;
    }

    @Override
    public QuestPackage getPackage() {
        return questPackage;
    }

    @Override
    public ConfigurationSection getSection() {
        return section;
    }

    @Override
    public SectionTraverser traverse() {
        return new DefaultSectionTraverser(this, parsers);
    }

    @Override
    public <T> Argument<T> get(final String path, final InstructionArgumentParser<T> parser) throws QuestException {
        return new DefaultArgument<>(variables, questPackage, section.getString(path),
                value -> parser.apply(variables, packageManager, questPackage, value));
    }

    @Override
    public <T> Argument<T> getOptional(final String path, final InstructionArgumentParser<T> parser, final T defaultValue) throws QuestException {
        if (!section.contains(path)) {
            return new DefaultArgument<>(defaultValue);
        }
        return new DefaultArgument<>(variables, questPackage, section.getString(path),
                value -> parser.apply(variables, packageManager, questPackage, value));
    }
}
