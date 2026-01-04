package org.betonquest.betonquest.compatibility.npc.citizens.event.move;

import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationEvent;
import net.citizensnpcs.api.ai.event.NavigationStuckEvent;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestTypeApi;
import org.betonquest.betonquest.api.quest.action.ActionID;
import org.betonquest.betonquest.compatibility.npc.citizens.CitizensWalkingListener;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Controls a Citizens-NPC movement.
 */
public class CitizensMoveController implements Listener, Predicate<NPC> {

    /**
     * Citizens NPC ID and their active move instance.
     */
    private final Map<Integer, MoveInstance> movingNpcs = new HashMap<>();

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Plugin to start tasks.
     */
    private final Plugin plugin;

    /**
     * Quest Type API.
     */
    private final QuestTypeApi questTypeApi;

    /**
     * Walking listener to check if movement is blocked by a conversation.
     */
    private final CitizensWalkingListener citizensWalkingListener;

    /**
     * Creates a new Citizens Move Controller.
     *
     * @param log                     logger instance for this class
     * @param plugin                  the plugin to start tasks
     * @param questTypeApi            the Quest Type API
     * @param citizensWalkingListener the walking listener for conversations
     */
    public CitizensMoveController(final BetonQuestLogger log, final Plugin plugin, final QuestTypeApi questTypeApi,
                                  final CitizensWalkingListener citizensWalkingListener) {
        this.log = log;
        this.plugin = plugin;
        this.questTypeApi = questTypeApi;
        this.citizensWalkingListener = citizensWalkingListener;
    }

    /**
     * Checks whenever this NPC is moving because of a 'move' event or not.
     *
     * @param npc NPC to check
     * @return true if the NPC is moving because of 'move' event, false if it's
     * standing or moving because other reasons
     */
    public boolean isNPCMoving(final NPC npc) {
        return npc.getOwningRegistry().equals(citizensWalkingListener.registry) && movingNpcs.containsKey(npc.getId());
    }

    /**
     * Stops the navigation for the NPC. It will still move to its current target.
     *
     * @param npc the npc to stop its current move control
     */
    public void stopNPCMoving(final NPC npc) {
        if (npc.getOwningRegistry().equals(citizensWalkingListener.registry)) {
            movingNpcs.remove(npc.getId());
        }
    }

    /**
     * Checks if you can talk to a npc or if it's moving because of a 'move' event and conversations are blocked.
     *
     * @param npc NPC to check
     * @return false if you can talk to the npc true if not
     */
    public boolean blocksTalking(final NPC npc) {
        return npc.getOwningRegistry().equals(citizensWalkingListener.registry)
                && movingNpcs.containsKey(npc.getId()) && movingNpcs.get(npc.getId()).moveData.blockConversations();
    }

    /**
     * Start a new path for the NPC if not already running.
     * <p>
     * It will execute the fail events of the old run for the given profile
     * and won't elaborate further.
     *
     * @param npc      the npc to move
     * @param profile  the profile the events will be executed with
     * @param moveData the move data used for the npc movement
     * @throws QuestException if there was an error getting the first location
     */
    public void startNew(final NPC npc, final Profile profile, final MoveData moveData) throws QuestException {
        if (!npc.getOwningRegistry().equals(citizensWalkingListener.registry)) {
            return;
        }
        final MoveInstance oldMoveInstance = movingNpcs.get(npc.getId());
        if (oldMoveInstance != null) {
            questTypeApi.events(profile, oldMoveInstance.moveData.failEvents());
            return;
        }
        final MoveInstance moveInstance = new MoveInstance(plugin, moveData, profile, npc);
        movingNpcs.put(npc.getId(), moveInstance);
    }

    /**
     * Handles a navigation cancel.
     *
     * @param event the navigation event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationCancelEvent event) {
        onContinue(event);
    }

    /**
     * Handles a navigation complete.
     *
     * @param event the navigation event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationCompleteEvent event) {
        onContinue(event);
    }

    /**
     * Handles a navigation stuck.
     *
     * @param event the navigation event to handle
     */
    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationStuckEvent event) {
        onContinue(event);
    }

    /**
     * Handles a navigation change.
     *
     * @param event the navigation event to handle
     */
    public void onContinue(final NavigationEvent event) {
        final NPC npc = event.getNPC();
        if (!npc.getOwningRegistry().equals(citizensWalkingListener.registry)) {
            return;
        }
        final int npcId = npc.getId();
        if (movingNpcs.containsKey(npcId)) {
            movingNpcs.get(npcId).onContinue(event);
        }
    }

    @Override
    public boolean test(final NPC npc) {
        return npc.getOwningRegistry().equals(citizensWalkingListener.registry) && blocksTalking(npc);
    }

    /**
     * All data required for the movement of an NPC.
     *
     * @param locations          the target location of the movement
     * @param waitTicks          the amount of ticks the NPC will wait before moving to the next location
     * @param doneEvents         the events to execute when the NPC reaches the last destination
     * @param failEvents         the events to execute when the NPC can't reach the last destination
     * @param blockConversations if the NPC will block conversation interaction while moving (includes wait time)
     */
    public record MoveData(Argument<List<Location>> locations, Argument<Number> waitTicks,
                           Argument<List<ActionID>> doneEvents, Argument<List<ActionID>> failEvents,
                           FlagArgument<Boolean> blockConversations) {

        /**
         * Creates a new MoveData instance.
         *
         * @param profile the profile to resolve the placeholders for
         * @return the resolved move data
         * @throws QuestException if there was an error resolving the placeholders
         */
        public ResolvedMoveData getResolvedMoveData(final Profile profile) throws QuestException {
            return new ResolvedMoveData(locations.getValue(profile), waitTicks.getValue(profile).longValue(),
                    doneEvents.getValue(profile), failEvents.getValue(profile), blockConversations.getValue(profile).orElse(false));
        }
    }

    /**
     * All data required for the movement of an NPC.
     *
     * @param locations          the target location of the movement
     * @param waitTicks          the amount of ticks the NPC will wait before moving to the next location
     * @param doneEvents         the events to execute when the NPC reaches the last destination
     * @param failEvents         the events to execute when the NPC can't reach the last destination
     * @param blockConversations if the NPC will block conversation interaction while moving (includes wait time)
     */
    public record ResolvedMoveData(List<Location> locations, long waitTicks,
                                   List<ActionID> doneEvents, List<ActionID> failEvents,
                                   boolean blockConversations) {

    }

    /**
     * An active navigation for a Citizens NPC.
     */
    private final class MoveInstance {

        /**
         * Plugin to start tasks.
         */
        private final Plugin plugin;

        /**
         * Move data used for the npc movement.
         */
        private final ResolvedMoveData moveData;

        /**
         * ID of the moving NPC.
         */
        private final int npcId;

        /**
         * The profile used to start this move instance and to work on.
         */
        private final Profile profile;

        /**
         * Iterator for the next target location.
         */
        private final ListIterator<Location> locationsIterator;

        private MoveInstance(final Plugin plugin, final MoveData moveData, final Profile profile, final NPC npc) throws QuestException {
            this.plugin = plugin;
            this.moveData = moveData.getResolvedMoveData(profile);
            this.npcId = npc.getId();
            this.profile = profile;
            this.locationsIterator = this.moveData.locations.listIterator(0);
            final Location firstLocation = locationsIterator.next();
            stopNPCMoving(npc);

            if (npc.isSpawned()) {
                if (citizensWalkingListener.isMovementPaused(npc)) {
                    citizensWalkingListener.setNewTargetLocation(npc, firstLocation);
                } else {
                    npc.getNavigator().setTarget(firstLocation);
                }
            } else {
                npc.spawn(firstLocation, SpawnReason.PLUGIN);
            }
        }

        /**
         * Moves the NPC to the next location, or the previous when it was the last.
         *
         * @param event the navigation event to handle
         */
        public void onContinue(final NavigationEvent event) {
            final NPC npc = event.getNPC();
            if (npc.getId() != npcId) {
                return;
            }
            if (citizensWalkingListener.isMovementPaused(npc)) {
                return;
            }
            if (event instanceof NavigationStuckEvent) {
                log.warn("The NPC '" + npc.getId() + "' navigation was stuck while navigating from '"
                        + locationToShortReadable(npc.getStoredLocation()) + "' to '"
                        + locationToShortReadable(event.getNavigator().getTargetAsLocation()) + "'. "
                        + "The configured stuck action from Citizens will now be called, BetonQuest will try to continue navigation.");
                return;
            }
            if (event instanceof final NavigationCancelEvent cancelEvent && cancelEvent.getCancelReason() != CancelReason.STUCK) {
                log.warn("The NPC '" + npc.getId() + "' navigation was cancelled at '"
                        + locationToShortReadable(npc.getStoredLocation()) + "' to '" + "'. "
                        + "Reason: " + cancelEvent.getCancelReason());
            }
            if (locationsIterator.hasNext()) {
                final Location next;
                next = locationsIterator.next();
                if (npc.isSpawned()) {
                    npc.getNavigator().setTarget(next);
                } else {
                    npc.spawn(next, SpawnReason.PLUGIN);
                }
                return;
            }
            delayDoneEvents(npc);
        }

        private String locationToShortReadable(final Location location) {
            return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getWorld().getName();
        }

        private void delayDoneEvents(final NPC npc) {
            npc.getNavigator().setPaused(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    npc.getNavigator().setPaused(false);
                    movingNpcs.remove(npcId);
                    questTypeApi.events(profile, moveData.doneEvents());
                }
            }.runTaskLater(plugin, moveData.waitTicks());
        }
    }
}
