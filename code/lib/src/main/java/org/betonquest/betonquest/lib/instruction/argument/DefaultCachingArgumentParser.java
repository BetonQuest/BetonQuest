package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.instruction.argument.CachingArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;

public class DefaultCachingArgumentParser<T> extends DecoratableArgumentParser<T> implements CachingArgumentParser<T> {

    /**
     * Create a new decoratable argument parser for an {@link InstructionArgumentParser}.
     *
     * @param argumentParser the wrapped argument parser
     */
    public DefaultCachingArgumentParser(final InstructionArgumentParser<T> argumentParser) {
        super(argumentParser);
    }
}
