package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.InstructionArgumentParser;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
    public T apply(final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        return argumentParser.apply(variables, packManager, pack, string);
    }

    @Override
    public <U> DecoratedArgumentParser<U> map(final QuestFunction<T, U> mapper) {
        return new DecoratableArgumentParser<>((variables, packManager, pack, string)
                -> mapper.apply(apply(variables, packManager, pack, string)));
    }

    @Override
    public DecoratedArgumentParser<T> validate(final ValueValidator<T> validator) {
        return validate(validator, "Invalid value '%s'");
    }

    @Override
    public DecoratedArgumentParser<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DecoratableArgumentParser<>((variables, packManager, pack, string) ->
                validateLocal(validator, errorMessage, variables, packManager, pack, string));
    }

    @Override
    public DecoratedArgumentParser<T> prefilter(final String expected, final T fixedValue) {
        return new DecoratableArgumentParser<>((variables, packManager, pack, string) ->
                expected.equalsIgnoreCase(string) ? fixedValue : apply(variables, packManager, pack, string));
    }

    @Override
    public DecoratedArgumentParser<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DecoratableArgumentParser<>((variables, packManager, pack, string) ->
                Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(variables, packManager, pack, string)));
    }

    private T validateLocal(final ValueValidator<T> checker, final String errorMessage, final Variables variables, final QuestPackageManager packManager, final QuestPackage pack, final String string) throws QuestException {
        final T value = apply(variables, packManager, pack, string);
        if (!checker.validate(value)) {
            throw new QuestException(errorMessage.formatted(string));
        }
        return value;
    }
}
