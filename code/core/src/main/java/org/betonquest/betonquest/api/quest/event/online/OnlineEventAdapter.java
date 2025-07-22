package org.betonquest.betonquest.api.quest.event.online;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

import java.util.Optional;

/**
 * Adapter to run an {@link OnlineEvent} via the {@link PlayerEvent} interface.
 * It supports a fallback if the player is not online.
 */
public final class OnlineEventAdapter implements PlayerEvent {
    /**
     * Event to run with the online profile.
     */
    private final OnlineEvent onlineEvent;

    /**
     * Fallback event to run if the player is not online.
     */
    private final PlayerEvent fallbackPlayerEvent;

    /**
     * Create an event that runs the given online event.
     * If the player is not online, it logs a message into the debug log.
     *
     * @param onlineEvent  event to run for online players
     * @param log          log to write to if the player is not online
     * @param questPackage quest package to reference in the log
     */
    public OnlineEventAdapter(final OnlineEvent onlineEvent, final BetonQuestLogger log, final QuestPackage questPackage) {
        this(onlineEvent, profile -> log.debug(
                questPackage,
                profile + " is offline, cannot fire event because it's not persistent."
        ));
    }

    /**
     * Create an event that runs the given online event if the player is online
     * and falls back to the fallback event otherwise.
     *
     * @param onlineEvent         event to run for online players
     * @param fallbackPlayerEvent fallback event to run for offline players
     */
    public OnlineEventAdapter(final OnlineEvent onlineEvent, final PlayerEvent fallbackPlayerEvent) {
        this.onlineEvent = onlineEvent;
        this.fallbackPlayerEvent = fallbackPlayerEvent;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final Optional<OnlineProfile> onlineProfile = profile.getOnlineProfile();
        if (onlineProfile.isPresent()) {
            onlineEvent.execute(onlineProfile.get());
        } else {
            fallbackPlayerEvent.execute(profile);
        }
    }
}
