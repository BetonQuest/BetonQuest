package org.betonquest.betonquest.compatibility.vault.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;

/**
 * Resolves to amount of money.
 */
public class MoneyVariable implements PlayerVariable {
    /**
     * Function to get the displayed money amount from a profile.
     */
    private final QuestFunction<Profile, String> function;

    /**
     * Create a new Money variable.
     *
     * @param function the function to get the displayed money amount from a profile
     */
    public MoneyVariable(final QuestFunction<Profile, String> function) {
        this.function = function;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return function.apply(profile);
    }
}
