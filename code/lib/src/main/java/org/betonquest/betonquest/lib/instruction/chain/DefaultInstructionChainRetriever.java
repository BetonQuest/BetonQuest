package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainRetriever;

import java.util.Optional;

/**
 * A default implementation of {@link InstructionChainRetriever}.
 *
 * @param <T> the variable type
 */
public class DefaultInstructionChainRetriever<T> implements InstructionChainRetriever<T> {

    /**
     * The instruction used to retrieve the variable.
     */
    protected final ChainableInstruction instruction;

    /**
     * The argument parser.
     */
    protected final InstructionArgumentParser<T> argument;

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the variable
     * @param argument    the argument parser
     */
    public DefaultInstructionChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument) {
        this.instruction = instruction;
        this.argument = argument;
    }

    @Override
    public Argument<T> get() throws QuestException {
        return instruction.getNext(argument);
    }

    @Override
    public Optional<Argument<T>> get(final String argumentKey) throws QuestException {
        return instruction.getOptional(argumentKey, argument);
    }

    @Override
    public Argument<T> get(final String argumentKey, final T defaultValue) throws QuestException {
        return instruction.getOptional(argumentKey, argument, defaultValue);
    }

    @Override
    public FlagArgument<T> getFlag(final String argumentKey, final T presenceDefaultValue) throws QuestException {
        return instruction.getFlag(argumentKey, argument, presenceDefaultValue);
    }
}
