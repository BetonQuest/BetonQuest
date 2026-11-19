package org.betonquest.betonquest.api.service.condition;

import java.util.Optional;

/**
 * Strategy to check if a certain combination of condition evaluations is valid.
 *
 * @since 3.2.0
 */
@FunctionalInterface
public interface TestStrategy {

    /**
     * Evaluates the given condition results to determine a combined result.
     * <p>
     * Returns a definite result as an {@link Optional} if one can be determined.
     * Once a definite result is returned, no further conditions will be evaluated.
     * If the result is still indefinite, an empty {@link Optional} is returned.
     * <p>
     * If {@code remaining} is {@code 0} and no definite result has been found, this method should return {@code false}.
     *
     * @param positive  the number of conditions that evaluated to {@code true}
     * @param negative  the number of conditions that evaluated to {@code false}
     * @param remaining the number of conditions that have not yet been evaluated
     * @return an {@link Optional} containing the definite result, or an empty {@link Optional} if indefinite
     * @since 3.2.0
     */
    Optional<Boolean> getResult(int positive, int negative, int remaining);
}
