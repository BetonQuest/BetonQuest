package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.quest.QuestException;

/**
 * A simple {@link org.bukkit.util.Consumer} that can throw a {@link QuestException}.
 *
 * @param <T> the type of the input to the operation
 */
@FunctionalInterface
public interface QuestConsumer<T> {
    /**
     * Performs this operation on the given argument.
     *
     * @param arg the input argument
     * @throws QuestException when the method execution fails
     */
    void accept(T arg) throws QuestException;
}
