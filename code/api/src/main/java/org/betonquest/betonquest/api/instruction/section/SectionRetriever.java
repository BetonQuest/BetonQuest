package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;

import java.util.Optional;

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

    /**
     * Retrieves the {@link Argument} for the section, returning an empty optional if the section is not present.
     *
     * @return the argument wrapped in an optional
     * @throws QuestException if the argument could not be resolved
     */
    Optional<Argument<T>> getOptional() throws QuestException;

    /**
     * This disables early validation of the parsing chain.
     * <br> <br>
     * <b>Avoid using this method if not necessary!</b>
     * <br>
     * Early validation ensures that configuration errors are detected as early as possible, usually on start or reload.
     * In many cases, this will cause the chain with all parsers to be called once before actually being accessed by
     * an ingame mechanic.
     * Because of that, it is being called multiple times, which may lead to undesired side effects.
     * Disabling the early validation will lead to errors only being spotted and logged after accessing
     * the corresponding code segment.
     * It poses the risk of faulty configurations remaining undetected for any amount of time.
     *
     * @return this section retriever with early validation disabled
     */
    SectionRetriever<T> withoutEarlyValidation();
}
