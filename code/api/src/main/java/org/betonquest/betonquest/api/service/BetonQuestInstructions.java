package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainRetriever;
import org.betonquest.betonquest.api.instruction.section.SectionInstruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.configuration.ConfigurationSection;

/**
 * The BetonQuest instructions service is responsible for the creating and resolution of instructions and arguments.
 * <br> <br>
 * {@link Instruction}s and {@link SectionInstruction}s allow to create {@link Argument}s by bootstrapping
 * different partioning, parsing, and validation steps together decorating a reusable function.
 * The resulting {@link Argument} also resolves any placeholders for varying {@link Profile}s
 * provided to {@link Argument#getValue(Profile)}.
 * <br> <br>
 * Right after creating an {@link Argument}, it attempts to validate the process by resolving the argument's value and
 * will throw an exception if the validation fails.
 * In the process of creating an instruction it may also throw a {@link QuestException} if the tokenizing fails.
 */
public interface BetonQuestInstructions {

    /**
     * Obtains a new {@link InstructionChainParser} for the given argument to be able to resolve its value.
     * The {@link QuestPackage} is only required to resolve placeholders correctly.
     * <br> <br>
     * The {@link InstructionChainParser} produced by this method will always resolve the provided argument parameter
     * even if different calls in the retrieval step are made.
     * <ul>
     *     <li>
     *         {@link InstructionChainRetriever#get()} will resolve the {@link Argument} normally
     *     </li>
     *     <li>
     *         {@link InstructionChainRetriever#get(String)} will resolve the {@link Argument} normally
     *         ignoring the given argument name.
     *     </li>
     *     <li>
     *         {@link InstructionChainRetriever#get(String, Object)} will resolve the {@link Argument} normally
     *         ignoring the given argument name and default value.
     *     </li>
     *     <li>
     *         {@link InstructionChainRetriever#getFlag(String, Object)} will resolve the {@link Argument} normally
     *         treating it as a present and defined flag ignoring the given argument name and default value.
     *     </li>
     * </ul>
     * <br> <br>
     * It does not provide an {@link Instruction} because there is no full and valid instruction string present.
     *
     * @param questPackage the package the argument will be resolved for
     * @param argument     the raw argument to parse
     * @return a new {@link InstructionChainParser} for the given argument
     */
    InstructionChainParser createForArgument(QuestPackage questPackage, String argument);

    /**
     * Obtains a new {@link InstructionChainParser} for the given argument to be able to resolve its value.
     * The {@link QuestPackage} is only required to resolve placeholders correctly.
     * The supplier will be resolved each time the parser is called allowing for dynamic arguments.
     * Besides that difference, see {@link #createForArgument(QuestPackage, String)} for more information.
     *
     * @param questPackage the package the argument will be resolved for
     * @param argument     the supplier for the raw argument to parse
     * @return a new {@link InstructionChainParser} for the given argument supplier
     */
    InstructionChainParser createForArgument(QuestPackage questPackage, QuestSupplier<String> argument);

    /**
     * Creates a new {@link Instruction} for the given {@link Identifier} and instruction string.
     * Use {@link #create(QuestPackage, String)} if no identifier is available.
     * <br> <br>
     * The tokenizer used to parse the instruction string is the same as the one used for action, conditions, etc.
     *
     * @param identifier  the identifier of the instruction
     * @param instruction the instruction string
     * @return a new {@link Instruction} for the given identifier and instruction string
     * @throws QuestException if the instruction cannot be created
     */
    Instruction create(Identifier identifier, String instruction) throws QuestException;

    /**
     * Creates a new {@link Instruction} for the given {@link QuestPackage} and instruction string.
     * Use {@link #create(Identifier, String)} to create an instruction for with an identifier instead.
     * <br> <br>
     * The tokenizer used to parse the instruction string is the same as the one used for action, conditions, etc.
     * <br> <br>
     * This instruction will not have a valid identifier and is solely for parsing the string.
     *
     * @param questPackage the package the instruction will be created for
     * @param instruction  the instruction string
     * @return a new {@link Instruction} for the given quest package and instruction string
     * @throws QuestException if the instruction cannot be created
     */
    Instruction create(QuestPackage questPackage, String instruction) throws QuestException;

    /**
     * Creates a new {@link Instruction} for the given {@link PlaceholderIdentifier} and placeholder string.
     * <br> <br>
     * The tokenizer used to parse the placeholder string expects the specific format of placeholders.
     *
     * @param placeholderIdentifier the identifier of the placeholder
     * @param placeholder           the placeholder string
     * @return a new {@link Instruction} for the given placeholder identifier and placeholder string
     * @throws QuestException if the placeholder instruction cannot be created
     */
    Instruction createPlaceholder(PlaceholderIdentifier placeholderIdentifier, String placeholder) throws QuestException;

    /**
     * Creates a new {@link SectionInstruction} for the given {@link QuestPackage} and {@link ConfigurationSection}.
     * <br> <br>
     * Instead of a tokenizer partitioning a string, every element of the configuration section will be treated either
     * as another section, a list of strings, or a string value.
     *
     * @param questPackage the package the section will be created for
     * @param section      the section to create the instruction for
     * @return a new {@link SectionInstruction} for the given quest package and configuration section
     * @throws QuestException if the section instruction cannot be created
     */
    SectionInstruction createSection(QuestPackage questPackage, ConfigurationSection section) throws QuestException;
}
