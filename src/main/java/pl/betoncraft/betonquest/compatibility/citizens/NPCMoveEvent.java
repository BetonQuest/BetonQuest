package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.event.NavigationCancelEvent;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.ai.event.NavigationEvent;
import net.citizensnpcs.api.ai.event.NavigationStuckEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;

/**
 * Moves the NPC to a specified location, optionally firing doneEvents when it's done.
 */
public class NPCMoveEvent extends QuestEvent implements Listener {

    private static final HashMap<Integer, NPCMoveEvent> movingNPCs = new HashMap<>();

    private final List<LocationData> locations;
    private final int npcId;
    private ListIterator<LocationData> locationsIterator;
    private final int waitTicks;
    private final EventID[] doneEvents;
    private final EventID[] failEvents;
    private String currentPlayer;
    private final boolean blockConversations;

    public NPCMoveEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        npcId = instruction.getInt();
        if (npcId < 0) {
            throw new InstructionParseException("NPC ID cannot be less than 0");
        }
        locations = instruction.getList(instruction::getLocation);
        if (locations.isEmpty()) {
            throw new InstructionParseException("Not enough arguments");
        }
        waitTicks = instruction.getInt(instruction.getOptional("wait"), 0);
        doneEvents = instruction.getList(instruction.getOptional("done"), instruction::getEvent).toArray(new EventID[0]);
        failEvents = instruction.getList(instruction.getOptional("fail"), instruction::getEvent).toArray(new EventID[0]);
        blockConversations = instruction.hasArgument("block");
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Checks whenever this NPC is moving because of a 'move' event or not.
     *
     * @param npc NPC to check
     * @return true if the NPC is moving because of 'move' event, false if it's
     * standing or moving because other reasons
     */
    public static boolean isNPCMoving(final NPC npc) {
        return movingNPCs.containsKey(npc.getId()) && movingNPCs.get(npc.getId()).currentPlayer != null;
    }

    public static void stopNPCMoving(final NPC npc) {
        if (movingNPCs.containsKey(npc.getId())) {
            movingNPCs.get(npc.getId()).currentPlayer = null;
        }
    }

    /**
     * Checks if you can talk to an npc or if it's moving because of a 'move' event and conversations are blocked
     *
     * @param npc NPC to check
     * @return false if you can talk to the npc true if not
     */
    public static boolean blocksTalking(final NPC npc) {
        if (!isNPCMoving(npc)) {
            return false;
        }
        return movingNPCs.get(npc.getId()).blockConversations;
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        // this event should not run if the player is offline
        if (PlayerConverter.getPlayer(playerID) == null) {
            currentPlayer = null;
            return null;
        }
        if (currentPlayer != null) {
            for (final EventID event : failEvents) {
                BetonQuest.event(playerID, event);
            }
            return null;
        }
        final NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
        if (npc == null) {
            throw new QuestRuntimeException("NPC with ID " + npcId + " does not exist");
        }
        if (!npc.isSpawned()) {
            return null;
        }
        locationsIterator = locations.listIterator(0);
        final LocationData firstLocation = locationsIterator.next();
        if (CitizensWalkingListener.getInstance().isMovementPaused(npc)) {
            CitizensWalkingListener.getInstance().setNewTargetLocation(npc, firstLocation.getLocation(playerID));
        } else {
            npc.getNavigator().setTarget(firstLocation.getLocation(playerID));
        }
        currentPlayer = playerID;
        movingNPCs.put(npc.getId(), this);
        return null;
    }

    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationCancelEvent event) {
        onContinue(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationCompleteEvent event) {
        onContinue(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onNavigation(final NavigationStuckEvent event) {
        onContinue(event);
    }

    public void onContinue(final NavigationEvent event) {
        final NPC npc = event.getNPC();
        if (npc.getId() != npcId) {
            return;
        }
        if (currentPlayer == null || locationsIterator == null) {
            return;
        }
        if (event instanceof NavigationStuckEvent || event instanceof NavigationCancelEvent) {
            LogUtils.getLogger().log(Level.WARNING, "The NPC was stucked, maybe the distance between two points was too high. "
                    + "This is a Citizens behavior, your NPC was teleported by Citizens, we continue the movement from this location.");
        }
        if (locationsIterator.hasNext()) {
            try {
                final Location next = locationsIterator.next().getLocation(currentPlayer);
                if (CitizensWalkingListener.getInstance().isMovementPaused(npc)) {
                    CitizensWalkingListener.getInstance().setNewTargetLocation(npc, next);
                } else {
                    npc.getNavigator().setTarget(next);
                }
            } catch (QuestRuntimeException e) {
                LogUtils.getLogger().log(Level.WARNING, "Error while NPC " + npc.getId() + " navigation: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
            return;
        }
        try {
            npc.getNavigator().setTarget(locationsIterator.previous().getLocation(currentPlayer));
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while finishing NPC " + npc.getId() + " navigation: " + e.getMessage());
            LogUtils.logThrowable(e);
        }
        npc.getNavigator().setPaused(true);
        new BukkitRunnable() {
            @Override
            public void run() {
                npc.getNavigator().setPaused(false);
                for (final EventID event : doneEvents) {
                    BetonQuest.event(currentPlayer, event);
                }
                locationsIterator = null;
                currentPlayer = null;
            }
        }.runTaskLater(BetonQuest.getInstance(), waitTicks);
    }

}
