package org.betonquest.betonquest.instruction.argument;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ID;
import org.jetbrains.annotations.Nullable;

/**
 * Creates an {@link ID} from a pack and string.
 *
 * @param <T> the type of the id
 */
@FunctionalInterface
public interface IDArgument<T extends ID> {
    /**
     * Creates a new ID.
     *
     * @param pack       the source pack
     * @param identifier the id name, potentially prefixed with a quest path
     * @return the newly created id
     * @throws QuestException when there is no such {@link T} in the resolved quest package or
     *                        when the {@link T} cannot be created
     */
    T convert(@Nullable QuestPackage pack, String identifier) throws QuestException;
}
