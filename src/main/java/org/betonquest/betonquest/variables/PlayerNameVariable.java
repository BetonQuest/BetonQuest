package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable extends Variable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    /**
     * Whether to resolve to the display name (see {@link Player#getDisplayName()}.
     */
    private final boolean display;

    /**
     * Creates a new PlayerNameVariable from the given instruction.
     *
     * @param instruction the instruction
     */
    public PlayerNameVariable(final Instruction instruction) {
        super(instruction);
        display = instruction.hasArgument("display");
    }

    @Override
    public String getValue(final Profile profile) {
        if (display) {
            final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
            if (onlineProfile.isEmpty()) {
                log.warn(instruction.getPackage(), profile.getPlayer().getName() + " is offline, cannot get display name.");
                return "";
            }
            return onlineProfile.get().getPlayer().getDisplayName();
        } else {
            return profile.getPlayer().getName();
        }
    }

}
