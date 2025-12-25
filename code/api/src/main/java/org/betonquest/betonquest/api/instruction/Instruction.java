package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * The Instruction. Primary object for input parsing.
 */
public interface Instruction extends ChainableInstruction, InstructionChainParser, InstructionParts {

    /**
     * Starts a new chain for the given argument to parse its value into an {@link Argument}.
     *
     * @param rawArgument the raw argument
     * @return a new {@link InstructionChainParser} based on this instruction starting for the given argument
     */
    default InstructionChainParser chainForArgument(final String rawArgument) {
        return chainForArgument(() -> rawArgument);
    }

    /**
     * Starts a new chain for the given argument supplier to parse its value into an {@link Argument}.
     * The parsing gets postponed until {@link Argument#getValue(Profile)} is called.
     *
     * @param rawArgumentSupplier the raw argument supplier
     * @return a new {@link InstructionChainParser} based on this instruction starting for the given argument
     */
    InstructionChainParser chainForArgument(QuestSupplier<String> rawArgumentSupplier);

    /**
     * Get the source QuestPackage.
     *
     * @return the package containing this instruction
     */
    QuestPackage getPackage();

    /**
     * Get {@link ArgumentParsers} with commonly used parsers.
     *
     * @return a provider of commonly used parsers
     */
    ArgumentParsers getParsers();

    /**
     * Get the {@link Identifier} of this instruction.
     *
     * @return the instruction identifier
     */
    Identifier getID();

    /**
     * Copy this instruction. The copy has no consumed arguments.
     *
     * @return a copy of this instruction
     */
    Instruction copy();

    /**
     * Copy this instruction but overwrite the ID of the copy. The copy has no consumed arguments.
     *
     * @param newID the ID to identify the copied instruction with
     * @return copy of this instruction with the new ID
     */
    Instruction copy(Identifier newID);

    /**
     * Checks if the instruction contains the argument.
     *
     * @param argument the argument to check
     * @return if the instruction contains that argument, ignoring cases
     */
    boolean hasArgument(String argument);
}
