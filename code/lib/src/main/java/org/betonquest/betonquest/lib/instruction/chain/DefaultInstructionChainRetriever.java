package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainRetriever;
import org.betonquest.betonquest.api.instruction.variable.Variable;

import java.util.List;
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
    public Variable<T> get() throws QuestException {
        return instruction.getNext(argument);
    }

    @Override
    public Variable<List<T>> getList() throws QuestException {
        return instruction.getNextList(argument);
    }

    @Override
    public Optional<Variable<T>> get(final String argumentKey) throws QuestException {
        return instruction.getOptional(argumentKey, argument);
    }

    @Override
    public Variable<T> get(final String argumentKey, final T defaultValue) throws QuestException {
        return instruction.getOptional(argumentKey, argument, defaultValue);
    }

    @Override
    public Optional<Variable<List<T>>> getList(final String argumentKey) throws QuestException {
        return instruction.getOptionalList(argumentKey, argument);
    }

    @Override
    public Variable<List<T>> getList(final String argumentKey, final List<T> defaultValue) throws QuestException {
        return instruction.getOptionalList(argumentKey, argument, defaultValue);
    }
}
