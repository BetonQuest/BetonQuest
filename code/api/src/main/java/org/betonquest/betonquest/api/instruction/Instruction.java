package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.instruction.argument.ArgumentParsers;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;

/**
 * The Instruction. Primary object for input parsing.
 */
public interface Instruction extends ChainableInstruction, InstructionParts {

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
    <T> Argument<T> get(String raw, InstructionArgumentParser<T> parser) throws QuestException;

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
    default <T> Argument<T> get(final String raw, final SimpleArgumentParser<T> parser) throws QuestException {
        return get(raw, (InstructionArgumentParser<T>) parser);
    }

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
