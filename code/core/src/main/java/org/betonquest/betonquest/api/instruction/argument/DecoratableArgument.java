package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A wrapper for {@link Argument} to turn it into a {@link DecoratedArgument}.
 *
 * @param <T> the type of the argument
 */
public class DecoratableArgument<T> implements DecoratedArgument<T> {

    /**
     * The wrapped {@link Argument}.
     */
    protected final Argument<T> argument;

    /**
     * Create a new {@link DecoratableArgument}.
     *
     * @param argument the {@link Argument} to wrap
     */
    public DecoratableArgument(final Argument<T> argument) {
        this.argument = argument;
    }

    @Override
    public T apply(final String string) throws QuestException {
        return argument.apply(string);
    }

    @Override
    public DecoratableArgument<T> validate(final ValueValidator<T> validator, final String errorMessage) {
        return new DecoratableArgument<>(string -> validateLocal(validator, string, errorMessage));
    }

    @Override
    public DecoratableArgument<T> validate(final ValueValidator<T> validator) {
        return new DecoratableArgument<>(string -> validateLocal(validator, string, "Invalid value '%s'"));
    }

    @Override
    public DecoratableArgument<T> prefilter(final String expected, final T fixedValue) {
        return new DecoratableArgument<>(string -> expected.equalsIgnoreCase(string) ? fixedValue : apply(string));
    }

    @Override
    public DecoratableArgument<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DecoratableArgument<>(string -> Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(string)));
    }

    private T validateLocal(final ValueValidator<T> checker, final String string, final String errorMessage) throws QuestException {
        final T value = apply(string);
        if (!checker.validate(value)) {
            throw new QuestException(errorMessage.formatted(string));
        }
        return value;
    }
}
