package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Stores Events and execute them.
 */
public class EventProcessor extends TypedQuestProcessor<EventID, EventAdapter> {
    /**
     * The quest package manager to use for the instruction.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * Create a new Event Processor to store events and execute them.
     *
     * @param log                 the custom logger for this class
     * @param questPackageManager the quest package manager to use for the instruction
     * @param eventTypes          the available event types
     */
    public EventProcessor(final BetonQuestLogger log, final QuestPackageManager questPackageManager, final EventTypeRegistry eventTypes) {
        super(log, eventTypes, "Event", "events");
        this.questPackageManager = questPackageManager;
    }

    @Override
    protected EventID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new EventID(questPackageManager, pack, identifier);
    }

    /**
     * Fires an event for the {@link Profile} if it meets the event's conditions.
     * If the profile is null, the event will be fired as a static event.
     *
     * @param profile the {@link Profile} for which the event must be executed or null
     * @param eventID ID of the event to fire
     * @return true if the event was run even if there was an exception during execution
     */
    public boolean execute(@Nullable final Profile profile, final EventID eventID) {
        final EventAdapter event = values.get(eventID);
        if (event == null) {
            log.warn(eventID.getPackage(), "Event " + eventID + " is not defined");
            return false;
        }
        if (profile == null) {
            log.debug(eventID.getPackage(), "Firing event " + eventID + " player independent");
        } else {
            log.debug(eventID.getPackage(),
                    "Firing event " + eventID + " for " + profile);
        }
        try {
            return event.fire(profile);
        } catch (final QuestException e) {
            log.warn(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
            return true;
        }
    }
}
