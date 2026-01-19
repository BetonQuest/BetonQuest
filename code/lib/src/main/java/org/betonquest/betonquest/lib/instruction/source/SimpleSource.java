package org.betonquest.betonquest.lib.instruction.source;

import org.betonquest.betonquest.api.instruction.source.ValueSource;
import org.jetbrains.annotations.Nullable;

/**
 * The default implementation of {@link ValueSource}.
 *
 * @param value the source to return
 * @param <T>   the type of the source
 */
public record SimpleSource<T>(@Nullable T value) implements ValueSource<T> {

    @Override
    @Nullable
    public T getValue() {
        return value();
    }
}
