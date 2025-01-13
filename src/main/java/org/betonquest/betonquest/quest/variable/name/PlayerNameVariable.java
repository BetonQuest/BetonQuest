package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.exceptions.QuestException;

/**
 * This variable resolves into the player's name. It can have optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable implements PlayerVariable {
    /**
     * The type of the variable.
     */
    private final PlayerNameType type;

    /**
     * Creates a new PlayerNameVariable from the given instruction.
     *
     * @param type the type to extract the variable value from the profile
     */
    public PlayerNameVariable(final PlayerNameType type) {
        this.type = type;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return type.extractValue(profile);
    }
}
