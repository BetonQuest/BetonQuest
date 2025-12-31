package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;

import java.util.List;
import java.util.function.Function;

/**
 * The {@link DecoratableSectionRetriever} for {@link List}s.
 *
 * @param <T> the type of the list's elements
 */
public interface ListSectionRetriever<T> extends DecoratableSectionRetriever<List<T>> {

    @Override
    ListSectionRetriever<T> prefilter(String expected, List<T> fixedValue);

    @Override
    ListSectionRetriever<T> validate(ValueValidator<List<T>> validator);

    @Override
    ListSectionRetriever<T> validate(ValueValidator<List<T>> validator, String errorMessage);

    @Override
    default ListSectionRetriever<T> invalidate(final ValueValidator<List<T>> validator) {
        return validate(value -> !validator.validate(value));
    }

    @Override
    default ListSectionRetriever<T> invalidate(final ValueValidator<List<T>> validator, final String errorMessage) {
        return validate(value -> !validator.validate(value), errorMessage);
    }

    /**
     * Ensures that the list is not empty and throws an error if it is.
     *
     * @return a new {@link ListSectionRetriever} with the new validation
     */
    ListSectionRetriever<T> notEmpty();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     *
     * @return a new {@link ListSectionRetriever} with the new validation
     */
    ListSectionRetriever<T> distinct();

    /**
     * Ensures that all elements of the list are distinct and throws an error if they are not.
     *
     * @param extractor the method to extract the information of T that is supposed to be unique
     * @param <U>       the element of T that is supposed to be unique across the list
     * @return a new {@link ListSectionRetriever} with the new validation
     */
    <U> ListSectionRetriever<T> distinct(Function<T, U> extractor);
}
