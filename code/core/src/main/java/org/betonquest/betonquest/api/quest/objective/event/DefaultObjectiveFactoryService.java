package org.betonquest.betonquest.api.quest.objective.event;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.bukkit.event.Event;

/**
 * Default implementation of the {@link ObjectiveFactoryService}.
 */
public class DefaultObjectiveFactoryService implements ObjectiveFactoryService {

    /**
     * The instruction that created this service.
     */
    private final QuestPackage questPackage;

    /**
     * The event service to request events from.
     */
    private final ObjectiveService eventService;

    /**
     * Creates a new objective service.
     *
     * @param questPackage the instruction that created this service
     * @param eventService the event service to request events from
     */
    public DefaultObjectiveFactoryService(final QuestPackage questPackage, final ObjectiveService eventService) {
        this.questPackage = questPackage;
        this.eventService = eventService;
    }

    @Override
    public <T extends Event> EventServiceSubscriptionBuilder<T> request(final Class<T> eventClass) {
        return eventService.request(eventClass).source(questPackage);
    }
}
