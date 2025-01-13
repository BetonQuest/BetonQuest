package org.betonquest.betonquest.api.quest.variable.online;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Optional;

/**
 * Adapter to resolve an {@link OnlineVariable} via the {@link PlayerVariable} interface.
 * It supports a fallback if the player is not online.
 */
public final class OnlineVariableAdapter implements PlayerVariable {
    /**
     * Variable to resolve with the online profile.
     */
    private final OnlineVariable onlineVariable;

    /**
     * Fallback variable to resolve if the player is not online.
     */
    private final PlayerVariable fallbackVariable;

    /**
     * Create a variable that resolves the given online variable.
     * If the player is not online it logs a message into the debug log.
     *
     * @param onlineVariable variable to resolve for online players
     * @param log            log to write to if the player is not online
     * @param questPackage   quest package to reference in the log
     */
    public OnlineVariableAdapter(final OnlineVariable onlineVariable, final BetonQuestLogger log, final QuestPackage questPackage) {
        this(onlineVariable, profile -> {
            log.debug(questPackage, profile + " is offline, cannot get variable value because it's not persistent.");
            return "";
        });
    }

    /**
     * Create a variable that resolves the given online variable if the player is online
     * and falls back to the fallback variable otherwise.
     *
     * @param onlineVariable   variable to resolve for online players
     * @param fallbackVariable fallback variable to resolve for offline players
     */
    public OnlineVariableAdapter(final OnlineVariable onlineVariable, final PlayerVariable fallbackVariable) {
        this.onlineVariable = onlineVariable;
        this.fallbackVariable = fallbackVariable;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            return onlineVariable.getValue(onlineProfile.get());
        }
        return fallbackVariable.getValue(profile);
    }
}
