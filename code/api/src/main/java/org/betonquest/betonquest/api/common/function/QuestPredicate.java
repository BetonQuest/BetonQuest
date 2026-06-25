package org.betonquest.betonquest.api.common.function;

import org.betonquest.betonquest.api.QuestException;

import java.util.Objects;

/**
 * A {@link java.util.function.Predicate} that may throw a {@link QuestException}
 *
 * @param <T> The type of the input argument
 * @since 3.1.0
 */
@FunctionalInterface
public interface QuestPredicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param value the argument to test
     * @return {@code true} if the input argument match the predicate,
     * otherwise {@code false}
     * @throws QuestException when the method execution fails
     * @since 3.1.0
     */
    boolean test(T value) throws QuestException;

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
     * @since 3.1.0
     */
    default QuestPredicate<T> and(final QuestPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (T t) -> test(t) && other.test(t);
    }

    /**
     * Returns a predicate that represents the logical negation of this
     * predicate.
     *
     * @return a predicate that represents the logical negation of this
     * predicate
     * @since 3.1.0
     */
    default QuestPredicate<T> negate() {
        return (T t) -> !test(t);
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
     * @since 3.1.0
     */
    @SuppressWarnings("PMD.ShortMethodName")
    default QuestPredicate<T> or(final QuestPredicate<? super T> other) {
        Objects.requireNonNull(other);
        return (T t) -> test(t) || other.test(t);
    }
}
