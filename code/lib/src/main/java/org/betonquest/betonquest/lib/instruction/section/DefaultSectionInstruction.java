package org.betonquest.betonquest.lib.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.instruction.section.SectionTraverser;
import org.betonquest.betonquest.api.instruction.source.DefaultedValueSource;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.betonquest.betonquest.lib.instruction.argument.DefaultChainableInstruction;
import org.betonquest.betonquest.lib.instruction.argument.DefaultInstructionChainParser;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation of {@link SectionInstruction}.
 */
public class DefaultSectionInstruction implements SectionInstruction {

    /**
     * The placeholder provider used to resolve placeholders in the section.
     */
    private final Placeholders placeholders;

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
     * The logger factory to use.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new DefaultSectionInstruction.
     *
     * @param parsers        the parsers used to parse the section
     * @param placeholders   the placeholder provider used to resolve placeholders in the section
     * @param packageManager the package manager used to resolve relative paths
     * @param questPackage   the quest package this instruction belongs to
     * @param section        the section to parse
     * @param loggerFactory  the logger factory to use
     */
    public DefaultSectionInstruction(final ArgumentParsers parsers, final Placeholders placeholders,
                                     final QuestPackageManager packageManager, final QuestPackage questPackage,
                                     final ConfigurationSection section, final BetonQuestLoggerFactory loggerFactory) {
        this.parsers = parsers;
        this.placeholders = placeholders;
        this.packageManager = packageManager;
        this.questPackage = questPackage;
        this.section = section;
        this.loggerFactory = loggerFactory;
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    private String buildPath(final ValueSource<List<String>> pathSource) {
        final List<String> path;
        if (pathSource instanceof final DefaultedValueSource<List<String>> defaultedValueSource) {
            path = defaultedValueSource.getOrDefault(value -> section.contains(buildPath(() -> value)));
        } else {
            path = pathSource.getValue();
        }
        if (path == null || path.isEmpty()) {
            return "";
        }
        if (path.size() == 1) {
            return path.get(0);
        }
        final Configuration root = section.getRoot();
        if (root == null) {
            throw new IllegalStateException("Root section is null for path '%s' in section '%s' for package '%s'"
                    .formatted(String.join(".", path), section.getCurrentPath(), questPackage));
        }
        return String.join(String.valueOf(root.options().pathSeparator()), path);
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
    public BetonQuestLoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    @Override
    public InstructionChainParser chainForArgument(final String argument) {
        final DefaultChainableInstruction instruction = new DefaultChainableInstruction(placeholders, packageManager, questPackage,
                () -> argument, val -> argument, val -> Map.entry(FlagState.DEFINED, val));
        return new DefaultInstructionChainParser(instruction, parsers);
    }

    @Override
    public SectionInstruction subSection(final String... path) throws QuestException {
        final String builtPath = buildPath(() -> List.of(path));
        if (!section.isConfigurationSection(builtPath)) {
            throw new QuestException("Section '%s' does not exist in section '%s' for package '%s'".formatted(builtPath, section.getCurrentPath(), questPackage));
        }
        return new DefaultSectionInstruction(parsers, placeholders, packageManager, questPackage, section.getConfigurationSection(builtPath), loggerFactory);
    }

    @Override
    public SectionInstruction cloneWithSection(final ConfigurationSection section) {
        return new DefaultSectionInstruction(parsers, placeholders, packageManager, questPackage, section, loggerFactory);
    }

    @Override
    public <T> Argument<T> get(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser,
                               final boolean pathMode, final boolean earlyValidation) throws QuestException {
        final String argumentPath = buildPath(path);
        if (!section.contains(argumentPath)) {
            throw new QuestException("Path '%s' does not exist in section '%s' for package '%s'".formatted(argumentPath, section.getCurrentPath(), questPackage));
        }
        return new DefaultArgument<>(placeholders, questPackage, pathMode ? argumentPath : section.getString(argumentPath),
                value -> parser.apply(placeholders, packageManager, questPackage, value), earlyValidation);
    }

    @Override
    public <T> Optional<Argument<T>> getOptional(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser, final boolean pathMode, final boolean earlyValidation) throws QuestException {
        final String argumentPath = buildPath(path);
        if (!section.contains(argumentPath)) {
            return Optional.empty();
        }
        return Optional.of(get(path, parser, pathMode, earlyValidation));
    }

    @Override
    public <T> Argument<T> getOptional(final ValueSource<List<String>> path, final InstructionArgumentParser<T> parser,
                                       final boolean pathMode, final boolean earlyValidation, final T defaultValue) throws QuestException {
        final String argumentPath = buildPath(path);
        if (!section.contains(argumentPath)) {
            return new DefaultArgument<>(defaultValue);
        }
        return get(path, parser, pathMode, earlyValidation);
    }
}
