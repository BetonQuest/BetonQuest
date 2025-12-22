package org.betonquest.betonquest.api.instruction.variable.resolver;

import org.betonquest.betonquest.api.QuestException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for the instruction to split method definitions.
 */
public interface Resolver {

    /**
     * Gets the next string.
     * <p>
     * Calling this method again will result in a different result.
     *
     * @return the following string
     * @throws QuestException when there is no part left
     */
    String nextElement() throws QuestException;

    /**
     * Gets an optional key:value instruction argument or null if the key is not present.
     *
     * @param prefix the prefix of the optional value without ":"
     * @return the value or null
     */
    @Nullable
    default String getValue(final String prefix) {
        return getValue(prefix, null);
    }

    /**
     * Gets an optional value or the default value if value is not present.
     *
     * @param prefix        the prefix of the optional value
     * @param defaultString the default value
     * @return the value or the default value
     */
    @Contract("_, !null -> !null")
    @Nullable
    String getValue(String prefix, @Nullable String defaultString);
}
