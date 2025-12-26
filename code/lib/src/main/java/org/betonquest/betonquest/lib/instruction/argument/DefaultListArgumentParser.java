package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.ListArgumentParser;

import java.util.List;
import java.util.function.Function;

/**
 * Decoratable {@link InstructionArgumentParser} for lists.
 *
 * @param <T> the type of the list's elements
 */
public class DefaultListArgumentParser<T> extends DecoratableArgumentParser<List<T>> implements ListArgumentParser<T> {

    /**
     * Create a new decoratable argument parser for an {@link DefaultListArgumentParser}.
     *
     * @param argumentParser the wrapped argument parser
     */
    public DefaultListArgumentParser(final InstructionArgumentParser<List<T>> argumentParser) {
        super(argumentParser);
    }

    @Override
    public ListArgumentParser<T> notEmpty() {
        return new DefaultListArgumentParser<>(super.invalidate(List::isEmpty));
    }

    @Override
    public ListArgumentParser<T> distinct() {
        return new DefaultListArgumentParser<>(
                super.validate(list -> list.stream().distinct().count() == list.size()));
    }

    @Override
    public <U> ListArgumentParser<T> distinct(final Function<T, U> extractor) {
        return new DefaultListArgumentParser<>(
                super.validate(list -> list.stream().map(extractor).distinct().count() == list.size()));
    }
}
