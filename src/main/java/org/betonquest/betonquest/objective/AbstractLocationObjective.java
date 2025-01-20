package org.betonquest.betonquest.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The AbstractLocationObjective class extends the Objective class and implements the Listener interface
 * to handle all movements of players in the game.
 * This abstract class serves as a base for objectives that are completed
 * when a player enters or exits a specific location.
 * It listens for various player events such as join, quit, death, respawn, teleport, and movement
 * to check the player's location.
 */
public abstract class AbstractLocationObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Should entry be checked instead of being inside the location of not.
     */
    private final boolean entry;

    /**
     * Should exit be checked instead of being inside the location of not.
     */
    private final boolean exit;

    /**
     * A map of players and if they are inside the location.
     */
    private final Map<UUID, Boolean> playersInsideRegion;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     * It initializes the entry and exit booleans and the playersInsideRegion map.
     *
     * @param log         the BetonQuestLogger object to be used in the constructor
     * @param instruction the Instruction object to be used in the constructor
     * @throws QuestException if there is an error while parsing the instruction
     */
    public AbstractLocationObjective(final BetonQuestLogger log, final Instruction instruction) throws QuestException {
        super(instruction);
        this.log = log;
        entry = instruction.hasArgument("entry");
        exit = instruction.hasArgument("exit");
        playersInsideRegion = new HashMap<>();
    }

    /**
     * The onPlayerJoin method listens for the PlayerJoinEvent and checks the player's location.
     *
     * @param event the PlayerJoinEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        checkLocation(event.getPlayer(), event.getPlayer().getLocation());
    }

    /**
     * The onPlayerQuit method listens for the PlayerQuitEvent and removes the player from the playersInsideRegion map.
     *
     * @param event the PlayerQuitEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        playersInsideRegion.remove(PlayerConverter.getID(event.getPlayer()).getProfileUUID());
    }

    /**
     * The onPlayerDeath method listens for the PlayerDeathEvent and checks the player's location.
     *
     * @param event the PlayerDeathEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        checkLocation(event.getEntity(), event.getEntity().getLocation());
    }

    /**
     * The onPlayerRespawn method listens for the PlayerRespawnEvent and checks the player's location.
     *
     * @param event the PlayerRespawnEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        checkLocation(event.getPlayer(), event.getRespawnLocation());
    }

    /**
     * The onPlayerTeleport method listens for the PlayerTeleportEvent and checks the player's location.
     *
     * @param event the PlayerTeleportEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        onPlayerMove(event);
    }

    /**
     * The onPlayerMove method listens for the PlayerMoveEvent and checks the player's location.
     *
     * @param event the PlayerMoveEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        checkLocation(event.getPlayer(), event.getTo());
    }

    /**
     * The onVehicleMove method listens for the VehicleMoveEvent and checks the player's location.
     *
     * @param event the VehicleMoveEvent to be used in the method
     */
    @EventHandler(ignoreCancelled = true)
    public void onVehicleMove(final VehicleMoveEvent event) {
        final List<Entity> passengers = event.getVehicle().getPassengers();
        for (final Entity passenger : passengers) {
            if (passenger instanceof final Player player) {
                checkLocation(player, event.getTo());
            }
        }
    }

    private void checkLocation(final Player player, final Location location) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(player);
        if (!containsPlayer(onlineProfile)) {
            return;
        }

        final boolean toInside = isInsideHandleException(location, onlineProfile);
        if (!entry && !exit) {
            if (toInside && checkConditions(onlineProfile)) {
                completeObjective(onlineProfile);
            }
            return;
        }

        checkLocationEnterExit(onlineProfile, toInside);
    }

    private void checkLocationEnterExit(final OnlineProfile onlineProfile, final boolean toInside) {
        if (!playersInsideRegion.containsKey(onlineProfile.getProfileUUID())) {
            playersInsideRegion.put(onlineProfile.getProfileUUID(), toInside);
            return;
        }

        final boolean fromInside = playersInsideRegion.get(onlineProfile.getProfileUUID());
        playersInsideRegion.put(onlineProfile.getProfileUUID(), toInside);

        if ((entry && toInside && !fromInside || exit && fromInside && !toInside) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
            playersInsideRegion.remove(onlineProfile.getProfileUUID());
        }
    }

    private boolean isInsideHandleException(final Location location, final OnlineProfile onlineProfile) {
        final AtomicBoolean toInsideAtomic = new AtomicBoolean();
        qeHandler.handle(() -> toInsideAtomic.set(isInside(onlineProfile, location)));
        return toInsideAtomic.get();
    }

    /**
     * Checks if the player at the given location is inside the location.
     *
     * @param onlineProfile the online profile of the player
     * @param location      the location to be checked
     * @return true if the player is inside the location, false otherwise
     * @throws QuestException if there is an error while checking if the player is inside the location
     */
    protected abstract boolean isInside(OnlineProfile onlineProfile, Location location) throws QuestException;
}
