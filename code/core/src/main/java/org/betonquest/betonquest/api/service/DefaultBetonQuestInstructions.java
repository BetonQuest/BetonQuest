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
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultChainableInstruction;
import org.betonquest.betonquest.lib.instruction.argument.DefaultInstructionChainParser;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestInstructions}.
 */
public class DefaultBetonQuestInstructions implements BetonQuestInstructions {

    /**
     * The {@link Placeholders} supplier.
     */
    private final Supplier<Placeholders> placeholders;

    /**
     * The {@link QuestPackageManager} supplier.
     */
    private final Supplier<QuestPackageManager> packageManager;

    /**
     * The {@link ArgumentParsers} supplier.
     */
    private final Supplier<ArgumentParsers> argumentParsers;

    /**
     * The {@link BetonQuestLoggerFactory} supplier.
     */
    private final Supplier<BetonQuestLoggerFactory> loggerFactory;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestInstructions}.
     *
     * @param placeholders    the {@link Placeholders} supplier
     * @param packageManager  the {@link QuestPackageManager} supplier
     * @param argumentParsers the {@link ArgumentParsers} supplier
     * @param loggerFactory   the {@link BetonQuestLoggerFactory} supplier
     */
    public DefaultBetonQuestInstructions(final Supplier<Placeholders> placeholders, final Supplier<QuestPackageManager> packageManager,
                                         final Supplier<ArgumentParsers> argumentParsers, final Supplier<BetonQuestLoggerFactory> loggerFactory) {
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
        final ChainableInstruction instruction = new DefaultChainableInstruction(placeholders.get(), packageManager.get(), questPackage,
                argument, key -> argument.get(), key -> Map.entry(FlagState.DEFINED, key));
        return new DefaultInstructionChainParser(instruction, argumentParsers.get());
    }

    @Override
    public Instruction create(final Identifier identifier, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager.get(), identifier.getPackage(), identifier, argumentParsers.get(), instruction);
    }

    @Override
    public Instruction create(final QuestPackage questPackage, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager.get(), questPackage, null, argumentParsers.get(), instruction);
    }

    @Override
    public Instruction createPlaceholder(final PlaceholderIdentifier placeholderIdentifier, final String placeholder) throws QuestException {
        return new PlaceholderInstruction(placeholders.get(), packageManager.get(), placeholderIdentifier.getPackage(), placeholderIdentifier, argumentParsers.get(), placeholder);
    }

    @Override
    public SectionInstruction createSection(final QuestPackage questPackage, final ConfigurationSection section) throws QuestException {
        return new DefaultSectionInstruction(argumentParsers.get(), placeholders.get(), packageManager.get(), questPackage, section, loggerFactory.get());
    }
}
