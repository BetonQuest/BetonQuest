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
     * Checks the given condition evaluations to determine a combined result.
     * <br> <br>
     * If the result is definite, an optional with the result should be returned.
     * After that no more condition may be evaluated.
     * <br>
     * If the result is unclear, an empty optional should be returned.
     * <br> <br>
     * If {@code remaining} is {@code 0} and there is no definite result, it should return {@code false}.
     *
     * @param positive  the amount of conditions which evaluated to {@code true}
     * @param negative  the amount of conditions which evaluated to {@code false}
     * @param remaining the amount of conditions which have not been evaluated yet
     * @return the definite result of the test, or an empty optional if it is indefinite
     * @since 3.2.0
     */
    Optional<Boolean> getResult(int positive, int negative, int remaining);
}
