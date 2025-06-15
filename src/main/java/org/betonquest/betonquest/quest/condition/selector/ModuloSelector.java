package org.betonquest.betonquest.quest.condition.selector;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.variable.Variable;

import javax.annotation.Nullable;

/**
 * Selector to check if a value is dividable by a divisor.
 */
public class ModuloSelector implements Selector {

    /**
     * The divisor to check the value against.
     */
    private final Variable<Number> divisor;

    /**
     * Creates a new ModuloSelector.
     *
     * @param divisor the divisor to check the value against
     */
    public ModuloSelector(final Variable<Number> divisor) {
        this.divisor = divisor;
    }

    @Override
    public boolean matches(@Nullable final Profile profile, final Number value) throws QuestException {
        return value.intValue() % divisor.getValue(profile).intValue() == 0;
    }
}
