package org.betonquest.betonquest.api.instruction.argument;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * A decorated {@link List} argument offering more options on top of the {@link DecoratedArgumentParser} itself.
 *
 * @param <T> the type of the list's elements
 */
public interface ListArgumentParser<T> extends DecoratedArgumentParser<List<T>> {

    /**
     * Ensures that the list is not empty and throws an error if it is.
     *
     * @return a new {@link ListArgumentParser}
     */
    ListArgumentParser<T> notEmpty();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     * Uses the {@link Stream#distinct()} method, so make sure that T works with that.
     * If T is a complex type, use {@link #distinct(Function)}
     *
     * @return a new {@link ListArgumentParser}
     */
    ListArgumentParser<T> distinct();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     * The extractor defines the method to get information of T that is supposed to be unique.
     * e.g.: <code>distinct(Map.Entry::getKey)</code>
     * will ensure that the key of a list of map entries will be distinct.
     *
     * @param extractor the method to extract the information of T that is supposed to be unique
     * @param <U>       the element of T that is supposed to be unique across the list
     * @return a new {@link ListArgumentParser}
     * @see #distinct()
     */
    <U> ListArgumentParser<T> distinct(Function<T, U> extractor);
}
