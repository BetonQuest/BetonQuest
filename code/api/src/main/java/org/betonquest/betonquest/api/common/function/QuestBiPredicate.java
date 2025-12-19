package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

import java.util.Objects;

/**
 * A {@link java.util.function.BiPredicate} that may throw a {@link QuestException}.
 *
 * @param <T> the type of the first input to the method
 * @param <U> the type of the second input to the method
 */
@FunctionalInterface
public interface QuestBiPredicate<T, U> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param first  the first input argument
     * @param second the second input argument
     * @return {@code true} if the input arguments match the predicate,
     * otherwise {@code false}
     * @throws QuestException when the method execution fails
     */
    boolean test(T first, U second) throws QuestException;

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * AND of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code false}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ANDed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * AND of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     * @throws QuestException       when the method execution fails
     */
    default QuestBiPredicate<T, U> and(final QuestBiPredicate<? super T, ? super U> other) throws QuestException {
        Objects.requireNonNull(other);
        return (T t, U u) -> test(t, u) && other.test(t, u);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     * @throws QuestException when the method execution fails
     */
    default QuestBiPredicate<T, U> negate() throws QuestException {
        return (T t, U u) -> !test(t, u);
    }

    /**
     * Returns a composed predicate that represents a short-circuiting logical
     * OR of this predicate and another.  When evaluating the composed
     * predicate, if this predicate is {@code true}, then the {@code other}
     * predicate is not evaluated.
     *
     * <p>Any exceptions thrown during evaluation of either predicate are relayed
     * to the caller; if evaluation of this predicate throws an exception, the
     * {@code other} predicate will not be evaluated.
     *
     * @param other a predicate that will be logically-ORed with this
     *              predicate
     * @return a composed predicate that represents the short-circuiting logical
     * OR of this predicate and the {@code other} predicate
     * @throws NullPointerException if other is null
     * @throws QuestException       when the method execution fails
     */
    @SuppressWarnings("PMD.ShortMethodName")
    default QuestBiPredicate<T, U> or(final QuestBiPredicate<? super T, ? super U> other) throws QuestException {
        Objects.requireNonNull(other);
        return (T t, U u) -> test(t, u) || other.test(t, u);
    }
}
