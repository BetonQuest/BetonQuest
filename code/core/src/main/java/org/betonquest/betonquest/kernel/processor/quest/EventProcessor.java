package org.betonquest.betonquest.kernel.processor.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.api.quest.event.EventID;
import org.betonquest.betonquest.kernel.processor.TypedQuestProcessor;
import org.betonquest.betonquest.kernel.processor.adapter.EventAdapter;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Stores Events and execute them.
 */
public class EventProcessor extends TypedQuestProcessor<EventID, EventAdapter> {

    /**
     * The Bukkit scheduler to run sync tasks.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new Event Processor to store events and execute them.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param log         the custom logger for this class
     * @param packManager the quest package manager to get quest packages from
     * @param eventTypes  the available event types
     * @param scheduler   the bukkit scheduler to run sync tasks
     * @param plugin      the plugin instance
     */
    public EventProcessor(final BetonQuestLogger log, final Variables variables, final QuestPackageManager packManager,
                          final EventTypeRegistry eventTypes, final BukkitScheduler scheduler, final Plugin plugin) {
        super(log, variables, packManager, eventTypes, "Event", "events");
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    protected EventID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new EventID(variables, packManager, pack, identifier);
    }

    /**
     * Fires multiple events for the {@link Profile} if they meet the events' conditions.
     * If the profile is null, the events will be fired as static events.
     *
     * @param profile  the {@link Profile} for which the events must be executed or null
     * @param eventIDs IDs of the events to fire
     * @return true if all events were run even if there was an exception during execution
     */
    public boolean executes(@Nullable final Profile profile, final Collection<EventID> eventIDs) {
        if (Bukkit.isPrimaryThread()) {
            return eventIDs.stream().allMatch(eventID -> execute(profile, eventID));
        }

        final List<EventID> syncList = new ArrayList<>();
        final List<EventID> asyncList = new ArrayList<>();
        eventIDs.forEach(id -> {
            final EventAdapter adapter = values.get(id);
            final boolean syncAsync = adapter != null && adapter.isPrimaryThreadEnforced();
            (syncAsync ? syncList : asyncList).add(id);
        });

        final Future<Boolean> syncFuture = syncList.isEmpty() ? CompletableFuture.completedFuture(true)
                : scheduler.callSyncMethod(plugin, () -> syncList.stream().allMatch(eventID -> execute(profile, eventID)));
        final boolean asyncResult = asyncList.stream().allMatch(eventID -> execute(profile, eventID));

        try {
            return asyncResult && syncFuture.get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return true;
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
        final EventAdapter event = values.get(eventID);
        if (event == null) {
            log.warn(eventID.getPackage(), "Event " + eventID + " is not defined");
            return false;
        }
        if (profile == null) {
            log.debug(eventID.getPackage(), "Firing event " + eventID + " player independent");
        } else {
            log.debug(eventID.getPackage(), "Firing event " + eventID + " for " + profile);
        }
        if (event.isPrimaryThreadEnforced() && !Bukkit.isPrimaryThread()) {
            return callEventSync(profile, eventID, event);
        }
        return callEvent(profile, eventID, event);
    }

    private boolean callEventSync(@Nullable final Profile profile, final EventID eventID, final EventAdapter event) {
        try {
            return scheduler.callSyncMethod(plugin, () -> callEvent(profile, eventID, event)).get();
        } catch (final InterruptedException | ExecutionException e) {
            log.reportException(e);
            return true;
        }
    }

    private boolean callEvent(@Nullable final Profile profile, final EventID eventID, final EventAdapter event) {
        try {
            return event.fire(profile);
        } catch (final QuestException e) {
            log.warn(eventID.getPackage(), "Error while firing '" + eventID + "' event: " + e.getMessage(), e);
            return true;
        }
    }
}
