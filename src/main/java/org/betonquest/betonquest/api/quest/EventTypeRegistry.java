package org.betonquest.betonquest.api.quest;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.quest.event.NullStaticEventFactory;
import org.betonquest.betonquest.quest.event.legacy.FromClassQuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactoryAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores the event types that can be used in BetonQuest.
 */
public class EventTypeRegistry {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Logger factory to create class specific logger for quest event factories.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Map of registered events.
     */
    private final Map<String, QuestEventFactory> eventTypes = new HashMap<>();

    /**
     * Create a new event type registry.
     *
     * @param log           the logger that will be used for logging
     * @param loggerFactory the logger factory to create a new logger for the legacy quest type factory created
     */
    public EventTypeRegistry(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory) {
        this.log = log;
        this.loggerFactory = loggerFactory;
    }

    /**
     * Registers an event with its name and the class used to create instances of the event.
     *
     * @param name       name of the event type
     * @param eventClass class object for the event
     * @deprecated replaced by {@link #registerEvent(String, EventFactory, StaticEventFactory)}
     */
    @Deprecated
    public void registerEvents(final String name, final Class<? extends QuestEvent> eventClass) {
        log.debug("Registering " + name + " event type");
        eventTypes.put(name, new FromClassQuestEventFactory<>(loggerFactory.create(eventClass), eventClass));
    }

    /**
     * Registers an event that does not support static execution with its name
     * and a factory to create new normal instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event
     */
    public void registerNonStaticEvent(final String name, final EventFactory eventFactory) {
        registerEvent(name, eventFactory, new NullStaticEventFactory());
    }

    /**
     * Registers an event with its name and a single factory to create both normal and
     * static instances of the event.
     *
     * @param name         name of the event
     * @param eventFactory factory to create the event and the static event
     * @param <T>          type of factory that creates both normal and static instances of the event.
     */
    public <T extends EventFactory & StaticEventFactory> void registerEvent(final String name, final T eventFactory) {
        registerEvent(name, eventFactory, eventFactory);
    }

    /**
     * Registers an event with its name and two factories to create normal and
     * static instances of the event.
     *
     * @param name               name of the event
     * @param eventFactory       factory to create the event
     * @param staticEventFactory factory to create the static event
     */
    public void registerEvent(final String name, final EventFactory eventFactory, final StaticEventFactory staticEventFactory) {
        log.debug("Registering " + name + " event type");
        eventTypes.put(name, new QuestEventFactoryAdapter(eventFactory, staticEventFactory));
    }

    /**
     * Fetches the factory to create the event registered with the given name.
     *
     * @param name the name of the event
     * @return a factory to create the event
     */
    @Nullable
    public QuestEventFactory getEventFactory(final String name) {
        return eventTypes.get(name);
    }

    /**
     * Gets the keys of all registered event types.
     *
     * @return the actual key set
     */
    public Set<String> keySet() {
        return eventTypes.keySet();
    }
}
