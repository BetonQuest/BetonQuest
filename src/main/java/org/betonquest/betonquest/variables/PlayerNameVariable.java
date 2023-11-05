package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;

import java.util.Optional;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 */
@SuppressWarnings("PMD.CommentRequired")
public class PlayerNameVariable extends Variable {

    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    private final boolean display;

    public PlayerNameVariable(final Instruction instruction) {
        super(instruction);
        display = instruction.hasArgument("display");
    }

    @Override
    public String getValue(final Profile profile) {
        if (display) {
            final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
            if (onlineProfile.isEmpty()) {
                log.warn(instruction.getPackage(), profile.getPlayer().getName() + " is offline, cannot get display name");
                return "";
            }
            return onlineProfile.get().getPlayer().getDisplayName();
        } else {
            return profile.getPlayer().getName();
        }
    }

}
