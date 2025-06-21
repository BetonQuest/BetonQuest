package org.betonquest.betonquest.quest.condition.selector;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Selector that checks if a value is in a list of integers.
 */
public class IntegerListSelector implements Selector {

    /**
     * The list of {@link Variable<Number>} that will be parsed to integers to check the value against.
     */
    private final List<Variable<Number>> values;

    /**
     * Creates a new IntegerListSelector.
     *
     * @param values the list of {@link Variable<Number>} that will be parsed to integers to check the value against
     */
    public IntegerListSelector(final List<Variable<Number>> values) {
        this.values = values;
    }

    @Override
    public boolean matches(@Nullable final Profile profile, final Number value) throws QuestException {
        final List<Integer> actualValues = new ArrayList<>();
        for (final Variable<Number> v : values) {
            final Integer val = v.getValue(profile).intValue();
            actualValues.add(val);
        }
        return actualValues.contains(value.intValue());
    }
}
