package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.variable.resolver.ArgumentResolver;
import org.betonquest.betonquest.api.instruction.variable.resolver.IdentifierArgumentResolver;
import org.betonquest.betonquest.api.instruction.variable.resolver.InstructionIdentifierArgumentResolver;
import org.betonquest.betonquest.api.instruction.variable.resolver.PackageArgumentResolver;

/**
 * The Instruction. Primary object for input parsing.
 */
public interface Instruction extends ChainableInstruction, InstructionParts, ArgumentResolver, PackageArgumentResolver, IdentifierArgumentResolver, InstructionIdentifierArgumentResolver {

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
