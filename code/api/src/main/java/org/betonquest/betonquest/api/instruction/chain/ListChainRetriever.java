package org.betonquest.betonquest.api.instruction.chain;

import org.betonquest.betonquest.api.instruction.ValueValidator;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * An extended {@link InstructionChainRetriever} offering additional methods
 * to modify the parsing process before retrieving the argument explicitly for the {@link List} type.
 *
 * @param <T> the type of the list's elements
 * @see InstructionChainRetriever
 * @see DecoratableChainRetriever
 */
public interface ListChainRetriever<T> extends DecoratableChainRetriever<List<T>> {

    @Override
    ListChainRetriever<T> prefilter(String expected, List<T> fixedValue);

    @Override
    ListChainRetriever<T> validate(ValueValidator<List<T>> validator);

    @Override
    ListChainRetriever<T> validate(ValueValidator<List<T>> validator, String errorMessage);

    @Override
    default ListChainRetriever<T> invalidate(final ValueValidator<List<T>> validator) {
        return validate(value -> !validator.validate(value));
    }

    @Override
    default ListChainRetriever<T> invalidate(final ValueValidator<List<T>> validator, final String errorMessage) {
        return validate(value -> !validator.validate(value), errorMessage);
    }

    /**
     * Ensures that the list is not empty and throws an error if it is.
     *
     * @return a new {@link ListChainRetriever}
     */
    ListChainRetriever<T> notEmpty();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     * Uses the {@link Stream#distinct()} method, so make sure that T works with that.
     * If T is a complex type, use {@link #distinct(Function)}
     *
     * @return a new {@link ListChainRetriever}
     */
    ListChainRetriever<T> distinct();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     * The extractor defines the method to get information of T that is supposed to be unique.
     * e.g.: <code>distinct(Map.Entry::getKey)</code>
     * will ensure that the key of a list of map entries will be distinct.
     *
     * @param extractor the method to extract the information of T that is supposed to be unique
     * @param <U>       the element of T that is supposed to be unique across the list
     * @return a new {@link ListChainRetriever}
     * @see #distinct()
     */
    <U> ListChainRetriever<T> distinct(Function<T, U> extractor);
}
