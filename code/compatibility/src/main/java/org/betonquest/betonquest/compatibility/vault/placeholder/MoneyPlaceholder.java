package org.betonquest.betonquest.compatibility.vault.placeholder;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;

/**
 * Resolves to amount of money.
 */
public class MoneyPlaceholder implements PlayerPlaceholder {

    /**
     * Function to get the displayed money amount from a profile.
     */
    private final QuestFunction<Profile, String> function;

    /**
     * Create a new Money placeholder.
     *
     * @param function the function to get the displayed money amount from a profile
     */
    public MoneyPlaceholder(final QuestFunction<Profile, String> function) {
        this.function = function;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return function.apply(profile);
    }
}
