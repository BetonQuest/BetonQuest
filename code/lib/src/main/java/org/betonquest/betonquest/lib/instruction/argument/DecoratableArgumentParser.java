package org.betonquest.betonquest.lib.instruction.argument;

import org.apache.commons.lang3.StringUtils;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.ListArgumentParser;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wrapper for {@link InstructionArgumentParser} to turn it into a {@link DecoratedArgumentParser}.
 *
 * @param <T> the type of the argument
 */
public class DecoratableArgumentParser<T> implements DecoratedArgumentParser<T> {

    /**
     * The wrapped {@link InstructionArgumentParser}.
     */
    private final InstructionArgumentParser<T> argumentParser;

    /**
     * Create a new decoratable argument parser for an {@link InstructionArgumentParser}.
     *
     * @param argumentParser the wrapped argument parser
     */
    public DecoratableArgumentParser(final InstructionArgumentParser<T> argumentParser) {
        this.argumentParser = argumentParser;
    }

    @Override
    public T apply(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return argumentParser.apply(placeholders, packManager, pack, string);
    }

    @Override
    public ListArgumentParser<T> list() {
        return new DefaultListArgumentParser<>(collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public <R> DecoratedArgumentParser<R> collect(final Collector<T, ?, R> collector) {
        return new DecoratableArgumentParser<>((placeholders, packManager, pack, string) ->
                parseCollect(placeholders, packManager, pack, string, collector));
    }

    @Override
    public <U> DecoratedArgumentParser<U> map(final QuestFunction<T, U> mapper) {
        return new DecoratableArgumentParser<>((placeholders, packManager, pack, string)
                -> mapper.apply(apply(placeholders, packManager, pack, string)));
    }

    @Override
    public DecoratedArgumentParser<T> validate(final ValueValidator<T> validator) {
        return validate(validator, "Invalid value '%s'");
    }

    @Override
    public DecoratedArgumentParser<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DecoratableArgumentParser<>((placeholders, packManager, pack, string) ->
                validateLocal(validator, errorMessage, placeholders, packManager, pack, string));
    }

    @Override
    public DecoratedArgumentParser<T> prefilter(final String expected, final T fixedValue) {
        return new DecoratableArgumentParser<>((placeholders, packManager, pack, string) ->
                expected.equalsIgnoreCase(string) ? fixedValue : apply(placeholders, packManager, pack, string));
    }

    @Override
    public DecoratedArgumentParser<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DecoratableArgumentParser<>((placeholders, packManager, pack, string) ->
                Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(placeholders, packManager, pack, string)));
    }

    private <R, A> R parseCollect(final Placeholders placeholders, final QuestPackageManager packManager, final QuestPackage pack,
                                  final String string, final Collector<T, A, R> collector) throws QuestException {
        final String[] elements = StringUtils.split(string, ",");
        final Stream.Builder<T> streamBuilder = Stream.builder();
        for (final String element : elements) {
            final T resolved = apply(placeholders, packManager, pack, element);
            streamBuilder.add(resolved);
        }
        return streamBuilder.build().collect(collector);
    }

    private T validateLocal(final ValueValidator<T> checker, final String errorMessage, final Placeholders placeholders,
                            final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final T value = apply(placeholders, packManager, pack, string);
        if (!checker.validate(value)) {
            throw new QuestException(errorMessage.formatted(string));
        }
        return value;
    }
}
