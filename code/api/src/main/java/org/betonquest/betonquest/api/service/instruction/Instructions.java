package org.betonquest.betonquest.api.service.instruction;

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
import org.jetbrains.annotations.Contract;

/**
 * The BetonQuest instructions service is responsible for the creation and resolution of instructions and arguments.
 * <br><br>
 * {@link Instruction}s and {@link SectionInstruction}s allow the creation of {@link Argument}s by combining
 * different partitioning, parsing, and validation steps to decorate a reusable function.
 * The resulting {@link Argument} resolves any placeholders for the {@link Profile} provided to
 * {@link Argument#getValue(Profile)}.
 * <br><br>
 * Immediately after creating an {@link Argument}, the service attempts to validate it by resolving its value,
 * throwing an exception if validation fails. Creating an instruction may also throw a {@link QuestException}
 * if tokenization fails.
 *
 * @since 3.0.0
 */
public interface Instructions {

    /**
     * Obtains a new {@link InstructionChainParser} for the given argument to enable resolving its value.
     * The {@link QuestPackage} is required to resolve placeholders correctly.
     * <br><br>
     * The {@link InstructionChainParser} produced by this method will always resolve the provided argument parameter,
     * even if different calls are made during the retrieval step:
     * <ul>
     *     <li>{@link InstructionChainRetriever#get()} resolves the {@link Argument} normally.</li>
     *     <li>{@link InstructionChainRetriever#get(String)} resolves the {@link Argument} normally,
     *         ignoring the specified argument name.</li>
     *     <li>{@link InstructionChainRetriever#get(String, Object)} resolves the {@link Argument} normally,
     *         ignoring the specified argument name and default value.</li>
     *     <li>{@link InstructionChainRetriever#getFlag(String, Object)} resolves the {@link Argument} normally,
     *         treating it as a defined flag and ignoring the specified argument name and default value.</li>
     * </ul>
     * <br>
     * This method does not provide an {@link Instruction} because no full and valid instruction string is present.
     *
     * @param questPackage the package the argument is resolved for
     * @param argument     the raw argument string to parse
     * @return a new {@link InstructionChainParser} for the given argument
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    InstructionChainParser createForArgument(QuestPackage questPackage, String argument);

    /**
     * Obtains a new {@link InstructionChainParser} for the given argument to enable resolving its value.
     * The {@link QuestPackage} is required to resolve placeholders correctly.
     * The supplier is resolved each time the parser is called, allowing for dynamic arguments.
     * <br><br>
     * Aside from the supplier, see {@link #createForArgument(QuestPackage, String)} for more information.
     *
     * @param questPackage the package the argument is resolved for
     * @param argument     the supplier for the raw argument to parse
     * @return a new {@link InstructionChainParser} for the given argument supplier
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    InstructionChainParser createForArgument(QuestPackage questPackage, QuestSupplier<String> argument);

    /**
     * Creates a new {@link Instruction} for the given {@link Identifier} and instruction string.
     * Use {@link #create(QuestPackage, String)} if no identifier is available.
     * <br><br>
     * The tokenizer used to parse the instruction string is the same as the one used for actions, conditions, etc.
     *
     * @param identifier  the identifier of the instruction
     * @param instruction the instruction string
     * @return a new {@link Instruction} for the given identifier and instruction string
     * @throws QuestException if the instruction cannot be created
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    Instruction create(Identifier identifier, String instruction) throws QuestException;

    /**
     * Creates a new {@link Instruction} for the given {@link QuestPackage} and instruction string.
     * Use {@link #create(Identifier, String)} to create an instruction with an identifier instead.
     * <br><br>
     * The tokenizer used to parse the instruction string is the same as the one used for actions, conditions, etc.
     * <br><br>
     * This instruction will not have a valid identifier and is intended solely for parsing the string.
     *
     * @param questPackage the package the instruction is created for
     * @param instruction  the instruction string
     * @return a new {@link Instruction} for the given quest package and instruction string
     * @throws QuestException if the instruction cannot be created
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    Instruction create(QuestPackage questPackage, String instruction) throws QuestException;

    /**
     * Creates a new {@link Instruction} for the given {@link PlaceholderIdentifier} and placeholder string.
     * <br><br>
     * The tokenizer used to parse the placeholder string expects the specific format of placeholders.
     *
     * @param placeholderIdentifier the identifier of the placeholder
     * @param placeholder           the placeholder string
     * @return a new {@link Instruction} for the given placeholder identifier and placeholder string
     * @throws QuestException if the placeholder instruction cannot be created
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    Instruction createPlaceholder(PlaceholderIdentifier placeholderIdentifier, String placeholder) throws QuestException;

    /**
     * Creates a new {@link SectionInstruction} for the given {@link QuestPackage} and {@link ConfigurationSection}.
     * <br><br>
     * Instead of a tokenizer partitioning a string, every element of the configuration section is treated
     * as another section, a list of strings, or a string value.
     *
     * @param questPackage the package the section is created for
     * @param section      the section to create the instruction for
     * @return a new {@link SectionInstruction} for the given quest package and configuration section
     * @throws QuestException if the section instruction cannot be created
     * @since 3.0.0
     */
    @Contract(pure = true, value = "!null, !null -> new")
    SectionInstruction createSection(QuestPackage questPackage, ConfigurationSection section) throws QuestException;
}
