package org.betonquest.betonquest.api.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.ValueChecker;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * A wrapper for {@link Argument} to turn it into a {@link DecoratedArgument}.
 *
 * @param <T> the type of the argument
 */
public class DecoratableArgument<T> implements DecoratedArgument<T> {

    /**
     * The wrapped argument.
     */
    private final Argument<T> argument;

    /**
     * Create a new decoratable argument.
     *
     * @param argument the argument to wrap
     */
    public DecoratableArgument(final Argument<T> argument) {
        this.argument = argument;
    }

    @Override
    public T apply(final String string) throws QuestException {
        return argument.apply(string);
    }

    @Override
    public DecoratableArgument<T> validate(final ValueChecker<T> checker) {
        return new DecoratableArgument<>(string -> {
            final T value = apply(string);
            checker.check(value);
            return value;
        });
    }

    @Override
    public DecoratableArgument<T> prefilter(final String expected, final T fixedValue) {
        return new DecoratableArgument<>(string -> expected.equalsIgnoreCase(string) ? fixedValue : apply(string));
    }

    @Override
    public DecoratableArgument<Optional<T>> prefilterOptional(final String expected, @Nullable final T fixedValue) {
        return new DecoratableArgument<>(string -> Optional.ofNullable(expected.equalsIgnoreCase(string) ? fixedValue : apply(string)));
    }
}
