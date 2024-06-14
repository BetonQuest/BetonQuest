package org.betonquest.betonquest.quest.registry.type;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.OnlinePlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerQuestFactory;
import org.betonquest.betonquest.api.quest.PlayerlessQuestFactory;
import org.betonquest.betonquest.api.quest.QuestFactory;
import org.betonquest.betonquest.api.quest.event.ComposedEvent;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.OnlinePlayerEvent;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.quest.QuestTypeAdapter;
import org.betonquest.betonquest.quest.event.ComposedEventFactoryAdapter;
import org.betonquest.betonquest.quest.legacy.FromClassLegacyTypeFactory;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.betonquest.betonquest.quest.legacy.QuestEventFactoryAdapter;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the event types that can be used in BetonQuest.
 */
public class EventTypeRegistry extends QuestTypeRegistry<Event, StaticEvent, ComposedEvent, OnlinePlayerEvent, QuestEvent> {
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
    protected LegacyTypeFactory<QuestEvent> getFromClassLegacyTypeFactory(
            final BetonQuestLogger log, final Class<? extends QuestEvent> questEventClass) {
        return new FromClassLegacyTypeFactory<>(log, questEventClass, "event");
    }

    @Override
    protected LegacyTypeFactory<QuestEvent> getLegacyFactoryAdapter(
            @Nullable final PlayerQuestFactory<Event> playerFactory,
            @Nullable final PlayerlessQuestFactory<StaticEvent> playerlessFactory,
            @Nullable final OnlinePlayerQuestFactory<OnlinePlayerEvent> onlinePlayerFactory) {
        return new QuestEventFactoryAdapter(playerFactory, playerlessFactory, onlinePlayerFactory);
    }

    @Override
    protected QuestTypeAdapter<ComposedEvent, Event, StaticEvent> getAdapter(final QuestFactory<ComposedEvent> factory) {
        return new ComposedEventFactoryAdapter(factory);
    }
}
