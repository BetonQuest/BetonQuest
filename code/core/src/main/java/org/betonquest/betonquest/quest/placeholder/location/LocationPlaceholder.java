package org.betonquest.betonquest.quest.placeholder.location;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.placeholder.OnlinePlaceholder;

/**
 * Provides information about a Player's Location.
 * <p>
 * Format:
 * {@code %location.<mode>.<precision>%}
 * <p>
 * Modes:<br>
 * * xyz - The x, y and z location of the npc, separated by spaces<br>
 * * x - The x location of the npc<br>
 * * y - The y location of the npc<br>
 * * z - The z location of the npc<br>
 * * world - The world location of the npc<br>
 * * yaw - The yaw of the npc<br>
 * * pitch - The pitch of the npc<br>
 * * ulfShort - The location of the npc in the form x;y;z;world<br>
 * * ulfLong - The location of the npc in the form x;y;z;world;yaw;pitch<br>
 * Precision is decimals of precision desired, defaults to 0.<br>
 */
public class LocationPlaceholder implements OnlinePlaceholder {

    /**
     * The mode of the location response required. Provides multiple output formats.
     */
    private final LocationFormationMode mode;

    /**
     * The decimals of precision required, defaults to 0.
     */
    private final Argument<Number> decimalPlaces;

    /**
     * Construct a new LocationPlaceholder that allows for resolution of information about a Player's Location.
     *
     * @param mode          The mode of the location response required.
     * @param decimalPlaces The decimals of precision required, defaults to 0.
     */
    public LocationPlaceholder(final LocationFormationMode mode, final Argument<Number> decimalPlaces) {
        this.mode = mode;
        this.decimalPlaces = decimalPlaces;
    }

    @Override
    public String getValue(final OnlineProfile onlineProfile) throws QuestException {
        return mode.getFormattedLocation(onlineProfile.getPlayer().getLocation(), decimalPlaces.getValue(onlineProfile).intValue());
    }
}
