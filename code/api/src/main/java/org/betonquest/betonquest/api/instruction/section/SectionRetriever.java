package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;

/**
 * The final step of the section instruction chain for key-value retrieval.
 * This class offers methods to retrieve the {@link Argument}.
 *
 * @param <T> the type of the argument
 */
public interface SectionRetriever<T> {

    /**
     * Retrieves the {@link Argument} for the section.
     *
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     */
    Argument<T> get() throws QuestException;

    /**
     * Retrieves the {@link Argument} for the section, returning a default value if the section is not present.
     *
     * @param defaultValue the default value to return if the section is not present
     * @return the argument
     * @throws QuestException if the argument could not be resolved
     */
    Argument<T> getOptional(T defaultValue) throws QuestException;
}
