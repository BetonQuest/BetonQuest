package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestSupplier;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;

/**
 * The Instruction. Primary object for input parsing.
 */
public interface Instruction extends ChainableInstruction, InstructionChainParser, InstructionParts {

    /**
     * Legacy implementation.
     * Takes an input and parses it against a given parser.
     *
     * @param raw    the raw input to parse
     * @param parser the parser to use
     * @param <T>    the type of the variable
     * @return the parsed variable
     * @throws QuestException if the input could not be parsed
     * @deprecated legacy implementation that shall only exist until the last remaining classes using it are reworked.
     */
    @Deprecated
    <T> Variable<T> get(String raw, InstructionArgumentParser<T> parser) throws QuestException;

    /**
     * Legacy implementation.
     * Takes an input and parses it against a given parser.
     *
     * @param raw    the raw input to parse
     * @param parser the parser to use
     * @param <T>    the type of the variable
     * @return the parsed variable
     * @throws QuestException if the input could not be parsed
     * @deprecated legacy implementation that shall only exist until the last remaining classes using it are reworked.
     */
    @Deprecated
    default <T> Variable<T> get(final String raw, final SimpleArgumentParser<T> parser) throws QuestException {
        return get(raw, (InstructionArgumentParser<T>) parser);
    }

    /**
     * Starts a new chain instruction to parse a variable from.
     * This may not be an instruction but just a part of it that represents the entire parsable content of a variable.
     *
     * @param rawValue the raw variable value to be parsed
     * @return a new {@link InstructionChainParser} starting an instruction chain
     */
    default InstructionChainParser chainVariable(final String rawValue) {
        return chainVariable(() -> rawValue);
    }

    /**
     * Starts a new chain instruction to parse a variable from.
     * This may not be an instruction but just a part of it that represents the entire parsable content of a variable.
     * The supplier will not be called until {@link Variable#getValue(Profile)} is called in the encapsuled value.
     *
     * @param rawValueSupplier the raw variable value supplier
     * @return a new {@link InstructionChainParser} starting an instruction chain
     */
    InstructionChainParser chainVariable(QuestSupplier<String> rawValueSupplier);

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
