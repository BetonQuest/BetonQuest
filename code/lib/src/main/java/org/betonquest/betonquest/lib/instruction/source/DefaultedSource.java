package org.betonquest.betonquest.lib.instruction.source;

import org.betonquest.betonquest.api.instruction.source.DefaultedValueSource;
import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Default implementation of {@link DefaultedValueSource}.
 *
 * @param source the source to return
 * @param def    the default source to return if the source is invalid
 * @param <T>    the type of the source
 */
public record DefaultedSource<T>(ValueSource<T> source, ValueSource<T> def) implements DefaultedValueSource<T> {

    @Override
    public ValueSource<T> getDefault() {
        return def;
    }

    @Override
    @Nullable
    public T getOrDefault(final Predicate<T> predicate) {
        final T value = getValue();
        if (predicate.test(value)) {
            return value;
        }
        final ValueSource<T> defaultSource = getDefault();
        if (defaultSource instanceof final DefaultedValueSource<T> defaultedValueSource) {
            return defaultedValueSource.getOrDefault(predicate);
        }
        return defaultSource.getValue();
    }

    @Override
    @Nullable
    public T getValue() {
        return source.getValue();
    }
}
