package org.betonquest.betonquest.lib.instruction.chain;

import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.chain.ChainableInstruction;

public class DefaultCachingChainRetriever<T> extends DefaultInstructionChainRetriever<T> {

    /**
     * Creates a new instruction chain retriever.
     *
     * @param instruction the instruction used to retrieve the argument
     * @param argument    the argument parser
     */
    public DefaultCachingChainRetriever(final ChainableInstruction instruction, final InstructionArgumentParser<T> argument) {
        super(instruction, argument);
    }
}
