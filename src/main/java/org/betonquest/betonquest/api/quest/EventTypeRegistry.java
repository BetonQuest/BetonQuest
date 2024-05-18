package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.quest.event.ComposedEventFactoryAdapter;
import org.betonquest.betonquest.quest.event.legacy.FromClassQuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactoryAdapter;

/**
 * Stores the event types that can be used in BetonQuest.
 */
public class EventTypeRegistry extends QuestTypeRegistry<Event, StaticEvent, QuestEvent, QuestEventFactory, ComposedEvent> {
    /**
     * Create a new event type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public EventTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        super(log, loggerFactory, "event");
    }

    @Override
    protected QuestEventFactory getFromClassLegacyTypeFactory(final BetonQuestLogger log, final Class<? extends QuestEvent> questEventClass) {
        return new FromClassQuestEventFactory<>(log, questEventClass);
    }

    @Override
    protected QuestEventFactory getLegacyFactoryAdapter(final QuestFactory<Event> eventFactory, final StaticQuestFactory<StaticEvent> staticEventFactory) {
        return new QuestEventFactoryAdapter(eventFactory, staticEventFactory);
    }

    @Override
    protected ComposedQuestTypeAdapter<ComposedEvent, Event, StaticEvent> getComposedAdapter(final ComposedQuestFactory<ComposedEvent> composedFactory) {
        return new ComposedEventFactoryAdapter(composedFactory);
    }
}
