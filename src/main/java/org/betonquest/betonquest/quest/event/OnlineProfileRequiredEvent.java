package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Decorator for events that do not support execution with offline players.
 */
public class OnlineProfileRequiredEvent implements Event {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(OnlineProfileRequiredEvent.class);

    /**
     * The event to execute.
     */
    private final Event event;

    /**
     * The quest package to use for reporting failed executions.
     */
    private final QuestPackage questPackage;

    /**
     * Wrap the given event to only be executed if the given player is online.
     *
     * @param event        event to execute
     * @param questPackage quest package to use for reporting execution failures
     */
    public OnlineProfileRequiredEvent(final Event event, final QuestPackage questPackage) {
        this.event = event;
        this.questPackage = questPackage;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getOnlineProfile().isPresent()) {
            event.execute(profile);
        } else {
            LOG.debug(questPackage, profile + " is offline, cannot fire event because it's not persistent.");
        }
    }
}
