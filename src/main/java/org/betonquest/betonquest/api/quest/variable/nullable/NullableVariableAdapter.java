package org.betonquest.betonquest.api.quest.variable.nullable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * An adapter to handle both the {@link PlayerVariable} and {@link PlayerlessVariable}
 * with one common implementation of the {@link NullableVariable}.
 */
public final class NullableVariableAdapter implements PlayerVariable, PlayerlessVariable {
    /**
     * Common null-safe variable implementation.
     */
    private final NullableVariable variable;

    /**
     * Create an adapter that resolves variables via the given common implementation.
     *
     * @param variable common null-safe variable implementation
     */
    public NullableVariableAdapter(final NullableVariable variable) {
        this.variable = variable;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return variable.getValue(profile);
    }

    @Override
    public String getValue() throws QuestException {
        return variable.getValue(null);
    }
}
