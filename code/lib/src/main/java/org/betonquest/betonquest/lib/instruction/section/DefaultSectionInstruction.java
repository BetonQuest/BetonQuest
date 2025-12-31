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
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

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

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private String buildPath(final List<String> path) {
        if (path.isEmpty()) {
            return "";
        }
        if (path.size() == 1) {
            return path.get(0);
        }
        final Configuration root = section.getRoot();
        return String.join(root == null ? "." : String.valueOf(root.options().pathSeparator()), path);
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
    public SectionTraverser read() {
        return new DefaultSectionTraverser(this, parsers);
    }

    @Override
    public SectionInstruction subSection(final String... path) throws QuestException {
        final String builtPath = buildPath(List.of(path));
        if (!section.isConfigurationSection(builtPath)) {
            throw new QuestException("Section '%s' does not exist".formatted(builtPath));
        }
        return new DefaultSectionInstruction(parsers, variables, packageManager, questPackage, section.getConfigurationSection(builtPath));
    }

    @Override
    public SectionInstruction cloneWithSection(final ConfigurationSection section) {
        return new DefaultSectionInstruction(parsers, variables, packageManager, questPackage, section);
    }

    @Override
    public <T> Argument<T> get(final List<String> path, final InstructionArgumentParser<T> parser,
                               final boolean pathMode) throws QuestException {
        final String argumentPath = buildPath(path);
        return new DefaultArgument<>(variables, questPackage, pathMode ? argumentPath : section.getString(argumentPath),
                value -> parser.apply(variables, packageManager, questPackage, value));
    }

    @Override
    public <T> Argument<T> getOptional(final List<String> path, final InstructionArgumentParser<T> parser,
                                       final boolean pathMode, final T defaultValue) throws QuestException {
        final String argumentPath = buildPath(path);
        if (!section.contains(argumentPath)) {
            return new DefaultArgument<>(defaultValue);
        }
        return get(path, parser, pathMode);
    }
}
