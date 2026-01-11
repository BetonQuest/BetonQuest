package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.section.DefaultSectionInstruction;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The default implementation of the {@link InstructionApi}.
 */
public class DefaultInstructionApi implements InstructionApi {

    /**
     * The placeholders to use.
     */
    private final QuestSupplier<Placeholders> placeholders;

    /**
     * The quest package manager to get quest packages from.
     */
    private final QuestPackageManager packageManager;

    /**
     * The argument parsers to use.
     */
    private final QuestSupplier<ArgumentParsers> argumentParsers;

    /**
     * The logger factory to use.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a new instruction api.
     *
     * @param placeholders    the placeholders to use
     * @param packageManager  the quest package manager to get quest packages from
     * @param argumentParsers the argument parsers to use
     * @param loggerFactory   the logger factory to use
     */
    public DefaultInstructionApi(final QuestSupplier<Placeholders> placeholders, final QuestPackageManager packageManager,
                                 final QuestSupplier<ArgumentParsers> argumentParsers, final BetonQuestLoggerFactory loggerFactory) {
        this.placeholders = placeholders;
        this.packageManager = packageManager;
        this.argumentParsers = argumentParsers;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Instruction createInstruction(final Identifier identifier, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager, identifier.getPackage(), identifier, argumentParsers.get(), instruction);
    }

    @Override
    public Instruction createInstruction(final QuestPackage questPackage, final String instruction) throws QuestException {
        return new DefaultInstruction(placeholders.get(), packageManager, questPackage, null, argumentParsers.get(), instruction);
    }

    @Override
    public SectionInstruction createSectionInstruction(final QuestPackage questPackage, final ConfigurationSection section) throws QuestException {
        return new DefaultSectionInstruction(argumentParsers.get(), placeholders.get(), packageManager, questPackage, section, loggerFactory);
    }
}
