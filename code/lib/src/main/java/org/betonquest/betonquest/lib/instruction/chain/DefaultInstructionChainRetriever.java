package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;
import org.betonquest.betonquest.api.instruction.chain.InstructionChainRetriever;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A default implementation of {@link InstructionChainRetriever}.
 *
 * @param <T> the type of the argument
 */
public class DefaultInstructionChainRetriever<T> implements InstructionChainRetriever<T> {

    /**
     * The instruction used to retrieve the argument.
     */
    protected final ChainableInstruction instruction;

    /**
     * The argument parser.
     */
    protected final InstructionArgumentParser<T> argument;

    /**
     * If the argument result should be cached if it does not contain placeholders.
     */
    protected final boolean cache;

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the argument
     * @param argument    the argument parser
     * @param cache       if the argument should be cached if it does not contain placeholders
     */
    public DefaultInstructionChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument, final boolean cache) {
        this.instruction = instruction;
        this.argument = argument;
        this.cache = cache; // TODO non api breaking constructor
    }

    @Override
    public Argument<T> get() throws QuestException {
        return instruction.getNext(argument, cache);
    }

    @Override
    public Optional<Argument<T>> get(final String argumentKey) throws QuestException {
        return instruction.getOptional(argumentKey, argument, cache);
    }

    @Override
    public Argument<T> get(final String argumentKey, final T defaultValue) throws QuestException {
        return instruction.getOptional(argumentKey, argument, defaultValue, cache);
    }

    @Override
    public FlagArgument<T> getFlag(final String argumentKey, final T presenceDefaultValue) throws QuestException {
        return instruction.getFlag(argumentKey, argument, presenceDefaultValue, cache);
    }

    @Override
    public Map<String, Argument<T>> getNamed() throws QuestException {
        return getNamed(key -> true);
    }

    @Override
    public Map<String, Argument<T>> getNamed(final Predicate<String> keyFilter) throws QuestException {
        return instruction.getNamed(argument, keyFilter, cache);
    }
}
