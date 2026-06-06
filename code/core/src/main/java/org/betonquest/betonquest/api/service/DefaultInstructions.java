package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.FlagState;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.PlaceholderInstruction;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.lib.instruction.argument.DefaultChainableInstruction;
import org.betonquest.betonquest.lib.instruction.argument.DefaultInstructionChainParser;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link Instructions}.
 */
public class DefaultInstructions implements Instructions {

    /**
     * The {@link PlaceholderManager} supplier.
     */
    private final Supplier<PlaceholderManager> placeholders;

    /**
     * The {@link QuestPackageManager} supplier.
     */
    private final QuestPackageManager packageManager;

    /**
     * The {@link ArgumentParsers} supplier.
     */
    private final Supplier<ArgumentParsers> argumentParsers;

    /**
     * The {@link BetonQuestLoggerFactory} supplier.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the {@link DefaultInstructions}.
     *
     * @param loggerFactory   the {@link BetonQuestLoggerFactory} supplier
     * @param packageManager  the {@link QuestPackageManager} supplier
     * @param placeholders    the {@link PlaceholderManager} supplier
     * @param argumentParsers the {@link ArgumentParsers} supplier
     */
    public DefaultInstructions(final BetonQuestLoggerFactory loggerFactory, final QuestPackageManager packageManager,
                               final Supplier<PlaceholderManager> placeholders, final Supplier<ArgumentParsers> argumentParsers) {
        this.placeholders = placeholders;
        this.packageManager = packageManager;
        this.argumentParsers = argumentParsers;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public InstructionChainParser createForArgument(final QuestPackage questPackage, final String argument) {
        return createForArgument(questPackage, () -> argument);
    }

    @Override
    public InstructionChainParser createForArgument(final QuestPackage questPackage, final QuestSupplier<String> argument) {
        final ChainableInstruction instruction = new DefaultChainableInstruction(placeholders.get(), packageManager, questPackage,
                argument, key -> argument.get(), key -> Map.entry(FlagState.DEFINED, key), predicate -> Map.of("", argument.get()));
        return new DefaultInstructionChainParser(instruction, argumentParsers.get());
    }

    @Override
    public Instruction create(final Identifier identifier, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager, identifier.getPackage(), identifier, argumentParsers.get(), instruction);
    }

    @Override
    public Instruction create(final QuestPackage questPackage, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager, questPackage, null, argumentParsers.get(), instruction);
    }

    @Override
    public Instruction createPlaceholder(final PlaceholderIdentifier placeholderIdentifier, final String placeholder) throws QuestException {
        return new PlaceholderInstruction(placeholders.get(), packageManager, placeholderIdentifier.getPackage(), placeholderIdentifier, argumentParsers.get(), placeholder);
    }

    @Override
    public SectionInstruction createSection(final QuestPackage questPackage, final ConfigurationSection section) throws QuestException {
        return new DefaultSectionInstruction(argumentParsers.get(), placeholders.get(), packageManager, questPackage, section, loggerFactory);
    }
}
