package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * This variable resolves into the player's name. It can have optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable implements PlayerVariable {
    /**
     * The type of the variable.
     */
    private final Variable<PlayerNameType> type;

    /**
     * Creates a new PlayerNameVariable from the given instruction.
     *
     * @param type the type to extract the variable value from the profile
     */
    public PlayerNameVariable(final Variable<PlayerNameType> type) {
        this.type = type;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return type.getValue(profile).extractValue(profile);
    }
}
