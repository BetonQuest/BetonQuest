package org.betonquest.betonquest.compatibility.vault.variable;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * Resolves to amount of money.
 */
public class MoneyVariable implements PlayerVariable {
    /**
     * Function to get the displayed money amount from a profile.
     */
    private final MoneyVariableFactory.QuestExceptionFunction<Profile, String> function;

    /**
     * Create a new Money variable.
     *
     * @param function the function to get the displayed money amount from a profile
     */
    public MoneyVariable(final MoneyVariableFactory.QuestExceptionFunction<Profile, String> function) {
        this.function = function;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return function.apply(profile);
    }
}
