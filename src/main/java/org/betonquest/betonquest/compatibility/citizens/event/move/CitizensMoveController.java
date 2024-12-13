package org.betonquest.betonquest.compatibility.citizens.event.move;

import net.citizensnpcs.api.ai.event.CancelReason;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationEvent;
import net.citizensnpcs.api.ai.event.NavigationStuckEvent;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.compatibility.citizens.CitizensWalkingListener;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Controls a Citizens-NPC movement.
 */
public class CitizensMoveController implements Listener {
    /**
     * Citizens NPC ID and their active move instance.
     */
    private final Map<Integer, MoveInstance> movingNpcs = new HashMap<>();

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new Citizens Move Controller.
     *
     * @param log logger instance for this class
     */
    public CitizensMoveController(final BetonQuestLogger log) {
        this.log = log;
    }

    /**
     * Checks whenever this NPC is moving because of a 'move' event or not.
     *
     * @param npc NPC to check
     * @return true if the NPC is moving because of 'move' event, false if it's
     * standing or moving because other reasons
     */
    public boolean isNPCMoving(final NPC npc) {
        return movingNpcs.containsKey(npc.getId());
    }

    /**
     * Stops the navigation for the NPC. It will still move to its current target.
     *
     * @param npc the npc to stop its current move control
     */
    public void stopNPCMoving(final NPC npc) {
        movingNpcs.remove(npc.getId());
    }

    /**
     * Checks if you can talk to a npc or if it's moving because of a 'move' event and conversations are blocked.
     *
     * @param npc NPC to check
     * @return false if you can talk to the npc true if not
     */
    public boolean blocksTalking(final NPC npc) {
        return movingNpcs.containsKey(npc.getId()) && movingNpcs.get(npc.getId()).moveData.blockConversations;
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
     * @throws QuestRuntimeException if there was an error getting the first location
     */
    public void startNew(final NPC npc, final Profile profile, final MoveData moveData) throws QuestRuntimeException {
        final MoveInstance oldMoveInstance = movingNpcs.get(npc.getId());
        if (oldMoveInstance != null) {
            for (final EventID event : oldMoveInstance.moveData.failEvents()) {
                BetonQuest.event(profile, event);
            }
            return;
        }
        final MoveInstance moveInstance = new MoveInstance(moveData, profile, npc);
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
        final int npcId = event.getNPC().getId();
        if (movingNpcs.containsKey(npcId)) {
            movingNpcs.get(npcId).onContinue(event);
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
     * @param sourcePackage      the quest package that started the movement, used for debug logging
     */
    public record MoveData(List<VariableLocation> locations, int waitTicks, EventID[] doneEvents,
                           EventID[] failEvents, boolean blockConversations, QuestPackage sourcePackage) {
    }

    /**
     * An active navigation for a Citizens NPC.
     */
    private final class MoveInstance {
        /**
         * Move data used for the npc movement.
         */
        private final MoveData moveData;

        /**
         * ID of the moving NPC.
         */
        private final int npcId;

        /**
         * The profile used to start this move instance and to work on.
         */
        private final Profile currentProfile;

        /**
         * Iterator for the next target location.
         */
        private final ListIterator<VariableLocation> locationsIterator;

        private MoveInstance(final MoveData moveData, final Profile profile, final NPC npc) throws QuestRuntimeException {
            this.moveData = moveData;
            this.npcId = npc.getId();
            this.currentProfile = profile;
            this.locationsIterator = moveData.locations.listIterator(0);
            final Location firstLocation = locationsIterator.next().getValue(profile);
            stopNPCMoving(npc);

            if (npc.isSpawned()) {
                if (CitizensWalkingListener.getInstance().isMovementPaused(npc)) {
                    CitizensWalkingListener.getInstance().setNewTargetLocation(npc, firstLocation);
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
            if (CitizensWalkingListener.getInstance().isMovementPaused(npc)) {
                return;
            }
            if (event instanceof NavigationStuckEvent) {
                log.warn("The NPC '" + npc.getId() + "' navigation was stuck while navigating from '"
                        + locationToShortReadable(npc.getStoredLocation()) + "' to '"
                        + locationToShortReadable(event.getNavigator().getTargetAsLocation()) + "'. "
                        + "The configured stuck action from Citizens will now be called, BetonQuest will try to continue navigation.");
                return;
            } else if (event instanceof final NavigationCancelEvent cancelEvent && cancelEvent.getCancelReason() != CancelReason.STUCK) {
                log.warn("The NPC '" + npc.getId() + "' navigation was cancelled at '"
                        + locationToShortReadable(npc.getStoredLocation()) + "' to '" + "'. "
                        + "Reason: " + cancelEvent.getCancelReason());
            }
            if (locationsIterator.hasNext()) {
                final Location next;
                try {
                    next = locationsIterator.next().getValue(currentProfile);
                } catch (final QuestRuntimeException e) {
                    log.warn(moveData.sourcePackage(), "Error while NPC " + npc.getId() + " navigation: " + e.getMessage(), e);
                    return;
                }
                if (npc.isSpawned()) {
                    npc.getNavigator().setTarget(next);
                } else {
                    npc.spawn(next, SpawnReason.PLUGIN);
                }
                return;
            }
            returnToStart(npc);
        }

        private String locationToShortReadable(final Location location) {
            return location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getWorld().getName();
        }

        private void returnToStart(final NPC npc) {
            try {
                npc.getNavigator().setTarget(locationsIterator.previous().getValue(currentProfile));
            } catch (final QuestRuntimeException e) {
                log.warn(moveData.sourcePackage(), "Error while finishing NPC " + npc.getId() + " navigation: " + e.getMessage(), e);
            }
            npc.getNavigator().setPaused(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    npc.getNavigator().setPaused(false);
                    movingNpcs.remove(npcId);
                    for (final EventID event : moveData.doneEvents()) {
                        BetonQuest.event(currentProfile, event);
                    }
                }
            }.runTaskLater(BetonQuest.getInstance(), moveData.waitTicks());
        }
    }
}
