package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainRetriever;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.jetbrains.annotations.Nullable;

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
     * The default value to use if the instruction fails.
     */
    @Nullable
    protected final T defaultValue;

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction  the instruction used to retrieve the variable
     * @param argument     the argument parser
     * @param defaultValue the nullable default value to use if the instruction fails
     */
    public DefaultInstructionChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument,
                                            @Nullable final T defaultValue) {
        this.instruction = instruction;
        this.argument = argument;
        this.defaultValue = defaultValue;
    }

    @Override
    public Variable<T> get() {
        return instruction.getNext(argument, defaultValue);
    }

    @Override
    public Variable<List<T>> getList() {
        return instruction.getNextList(argument, defaultValue);
    }

    @Override
    public Variable<Optional<T>> get(final String argumentKey) {
        return instruction.getOptional(argumentKey, argument, defaultValue);
    }

    @Override
    public Variable<Optional<List<T>>> getList(final String argumentKey) {
        return instruction.getOptionalList(argumentKey, argument, defaultValue);
    }

    @Override
    public InstructionChainRetriever<T> def(final T defaultValue) {
        return new DefaultInstructionChainRetriever<>(instruction, argument, defaultValue);
    }
}
