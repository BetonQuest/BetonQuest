package org.betonquest.betonquest.quest.placeholder.name;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;

/**
 * This placeholder resolves into the player's name. It can have optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNamePlaceholder implements PlayerPlaceholder {

    /**
     * The type of the placeholder.
     */
    private final Argument<PlayerNameType> type;

    /**
     * Creates a new PlayerNamePlaceholder from the given instruction.
     *
     * @param type the type to extract the placeholder value from the profile
     */
    public PlayerNamePlaceholder(final Argument<PlayerNameType> type) {
        this.type = type;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return type.getValue(profile).extractValue(profile);
    }
}
