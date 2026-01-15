package org.betonquest.betonquest.api.instruction.section;

import org.betonquest.betonquest.api.instruction.ValueValidator;

/**
 * A decorated {@link DecoratableSectionRetriever} offering additional
 * methods to modify the parsing process for {@link Number}s.
 */
public interface NumberSectionRetriever extends DecoratableSectionRetriever<Number> {

    @Override
    NumberSectionRetriever prefilter(String expected, Number fixedValue);

    @Override
    NumberSectionRetriever validate(ValueValidator<Number> validator);

    @Override
    NumberSectionRetriever validate(ValueValidator<Number> validator, String errorMessage);

    @Override
    default NumberSectionRetriever invalidate(final ValueValidator<Number> validator) {
        return validate(value -> !validator.validate(value));
    }

    @Override
    default NumberSectionRetriever invalidate(final ValueValidator<Number> validator, final String errorMessage) {
        return validate(value -> !validator.validate(value), errorMessage);
    }

    /**
     * Ensures that the parsed number is at least the given value.
     *
     * @param min the inclusive minimum value
     * @return a new {@link NumberSectionRetriever} with the new validation
     */
    NumberSectionRetriever atLeast(int min);

    /**
     * Ensures that the parsed number is at most the given value.
     *
     * @param max the inclusive maximum value
     * @return a new {@link NumberSectionRetriever} with the new validation
     */
    NumberSectionRetriever atMost(int max);

    /**
     * Ensures that the parsed number is between the given values.
     *
     * @param min the inclusive minimum value
     * @param max the exclusive maximum value
     * @return a new {@link NumberSectionRetriever} with the new validation
     */
    NumberSectionRetriever inRange(int min, int max);
}
