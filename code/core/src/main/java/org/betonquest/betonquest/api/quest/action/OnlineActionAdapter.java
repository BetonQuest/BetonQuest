package org.betonquest.betonquest.api.quest.action;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;

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
     */
    public OnlineActionAdapter(final OnlineAction onlineAction) {
        this(onlineAction, profile -> {
            throw new QuestException("Cannot fire an online action for the offline player '%s'".formatted(profile));
        });
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
