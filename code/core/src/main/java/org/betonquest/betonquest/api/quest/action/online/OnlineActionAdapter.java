package org.betonquest.betonquest.api.quest.action.online;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

import java.util.Optional;

/**
 * Adapter to run an {@link OnlineAction} via the {@link PlayerAction} interface.
 * It supports a fallback if the player is not online.
 */
public final class OnlineActionAdapter implements PlayerAction {

    /**
     * Action to run with the online profile.
     */
    private final OnlineAction onlineAction;

    /**
     * Fallback action to run if the player is not online.
     */
    private final PlayerAction fallbackPlayerAction;

    /**
     * Create an action that runs the given online action.
     * If the player is not online, it logs a message into the debug log.
     *
     * @param onlineAction action to run for online players
     * @param log          log to write to if the player is not online
     * @param questPackage quest package to reference in the log
     */
    public OnlineActionAdapter(final OnlineAction onlineAction, final BetonQuestLogger log, final QuestPackage questPackage) {
        this(onlineAction, profile -> log.debug(
                questPackage,
                profile + " is offline, cannot fire action because it's not persistent."
        ));
    }

    /**
     * Create an action that runs the given online action if the player is online
     * and falls back to the fallback action otherwise.
     *
     * @param onlineAction         action to run for online players
     * @param fallbackPlayerAction fallback action to run for offline players
     */
    public OnlineActionAdapter(final OnlineAction onlineAction, final PlayerAction fallbackPlayerAction) {
        this.onlineAction = onlineAction;
        this.fallbackPlayerAction = fallbackPlayerAction;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            onlineAction.execute(onlineProfile.get());
        } else {
            fallbackPlayerAction.execute(profile);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return onlineAction.isPrimaryThreadEnforced() || fallbackPlayerAction.isPrimaryThreadEnforced();
    }
}
