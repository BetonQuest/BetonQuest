package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

/**
 * A simple {@link java.util.function.Supplier} that can throw a {@link QuestException}.
 *
 * @param <T> the type of results supplied by this supplier
 */
@FunctionalInterface
public interface QuestSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws QuestException when the method execution fails
     */
    T get() throws QuestException;
}
