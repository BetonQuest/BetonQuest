package org.betonquest.betonquest.quest.registry.processor;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.bstats.CompositeInstructionMetricsSupplier;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.quest.event.legacy.QuestEventFactory;
import org.betonquest.betonquest.quest.registry.type.EventTypeRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Stores Events and execute them.
 */
public class EventProcessor extends QuestProcessor<EventID, QuestEvent> {
    /**
     * Available Event types.
     */
    private final EventTypeRegistry types;

    /**
     * Create a new Event Processor to store events and execute them.
     *
     * @param log        the custom logger for this class
     * @param eventTypes the available event types
     */
    public EventProcessor(final BetonQuestLogger log, final EventTypeRegistry eventTypes) {
        super(log);
        this.types = eventTypes;
    }

    /**
     * Gets the bstats metric supplier for registered and active types.
     *
     * @return the metric with its type identifier
     */
    public Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier() {
        return Map.entry("events", new CompositeInstructionMetricsSupplier<>(values::keySet, types::keySet));
    }

    @SuppressWarnings("PMD.CognitiveComplexity")
    @Override
    public void load(final QuestPackage pack) {
        final ConfigurationSection section = pack.getConfig().getConfigurationSection("events");
        if (section != null) {
            final String packName = pack.getQuestPath();
            for (final String key : section.getKeys(false)) {
                if (key.contains(" ")) {
                    log.warn(pack, "Event name cannot contain spaces: '" + key + "' (in " + packName + " package)");
                    continue;
                }
                final EventID identifier;
                try {
                    identifier = new EventID(pack, key);
                } catch (final ObjectNotFoundException e) {
                    log.warn(pack, "Error while loading event '" + packName + "." + key + "': " + e.getMessage(), e);
                    continue;
                }
                final String type;
                try {
                    type = identifier.getInstruction().getPart(0);
                } catch (final InstructionParseException e) {
                    log.warn(pack, "Objective type not defined in '" + packName + "." + key + "'", e);
                    continue;
                }
                final QuestEventFactory eventFactory = types.getFactory(type);
                if (eventFactory == null) {
                    log.warn(pack, "Event type " + type + " is not registered, check if it's"
                            + " spelled correctly in '" + identifier + "' event.");
                    continue;
                }

                try {
                    final QuestEvent event = eventFactory.parseEventInstruction(identifier.getInstruction());
                    values.put(identifier, event);
                    log.debug(pack, "  Event '" + identifier + "' loaded");
                } catch (final InstructionParseException e) {
                    log.warn(pack, "Error in '" + identifier + "' event (" + type + "): " + e.getMessage(), e);
                }
            }
        }
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
        final QuestEvent event = values.get(eventID);
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
        } catch (final QuestRuntimeException e) {
            log.warn(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
            return true;
        }
    }
}
