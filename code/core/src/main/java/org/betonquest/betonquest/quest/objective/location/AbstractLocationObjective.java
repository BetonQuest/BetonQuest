package org.betonquest.betonquest.quest.objective.location;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
 * The AbstractLocationObjective class extends the Objective class handling all movements of players in the game.
 * This abstract class serves as a base for objectives that are completed
 * when a player enters or exits a specific location.
 * It listens for various player events such as join, quit, death, respawn, teleport, and movement
 * to check the player's location.
 * To register the required events, the {@link #registerLocationEvents(ObjectiveService)} method is called.
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class AbstractLocationObjective extends DefaultObjective {

    /**
     * Should entry be checked instead of being inside the location of not.
     */
    private final FlagArgument<Boolean> entry;

    /**
     * Should exit be checked instead of being inside the location of not.
     */
    private final FlagArgument<Boolean> exit;

    /**
     * A map of players and if they are inside the location.
     */
    private final Map<UUID, Boolean> playersInsideRegion;

    /**
     * The constructor takes an Instruction object as a parameter and throws an QuestException.
     * It initializes the entry and exit booleans and the playersInsideRegion map.
     *
     * @param service the ObjectiveFactoryService to be used in the constructor
     * @throws QuestException if there is an error while parsing the instruction
     */
    public AbstractLocationObjective(final ObjectiveService service) throws QuestException {
        super(service);
        entry = service.getInstruction().bool().getFlag("entry", true);
        exit = service.getInstruction().bool().getFlag("exit", true);
        playersInsideRegion = new HashMap<>();
    }

    /**
     * Registers the location events.
     *
     * @param service the ObjectiveFactoryService to be used in the method
     * @throws QuestException if there is an error while registering the events
     */
    public void registerLocationEvents(final ObjectiveService service) throws QuestException {
        service.request(PlayerJoinEvent.class).onlineHandler(this::onPlayerJoin)
                .player(PlayerJoinEvent::getPlayer).subscribe(true);
        service.request(PlayerQuitEvent.class).onlineHandler(this::onPlayerQuit)
                .player(PlayerQuitEvent::getPlayer).subscribe(true);
        service.request(PlayerDeathEvent.class).onlineHandler(this::onPlayerDeath)
                .player(PlayerDeathEvent::getPlayer).subscribe(true);
        service.request(PlayerRespawnEvent.class).onlineHandler(this::onPlayerRespawn)
                .player(PlayerRespawnEvent::getPlayer).subscribe(true);
        service.request(PlayerTeleportEvent.class).onlineHandler(this::onPlayerTeleport)
                .player(PlayerTeleportEvent::getPlayer).subscribe(true);
        service.request(PlayerMoveEvent.class).onlineHandler(this::onPlayerMove)
                .player(PlayerMoveEvent::getPlayer).subscribe(true);
        service.request(VehicleMoveEvent.class).handler(this::onVehicleMove).ignoreConditions().subscribe(true);
    }

    /**
     * The onPlayerJoin method listens for the PlayerJoinEvent and checks the player's location.
     *
     * @param event         the PlayerJoinEvent to be used in the method
     * @param onlineProfile the online profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerJoin(final PlayerJoinEvent event, final OnlineProfile onlineProfile) throws QuestException {
        checkLocation(onlineProfile, event.getPlayer().getLocation());
    }

    /**
     * The onPlayerQuit method listens for the PlayerQuitEvent and removes the player from the playersInsideRegion map.
     *
     * @param event         the PlayerQuitEvent to be used in the method
     * @param onlineProfile the online profile of the player
     */
    public void onPlayerQuit(final PlayerQuitEvent event, final OnlineProfile onlineProfile) {
        playersInsideRegion.remove(onlineProfile.getProfileUUID());
    }

    /**
     * The onPlayerDeath method listens for the PlayerDeathEvent and checks the player's location.
     *
     * @param event         the PlayerDeathEvent to be used in the method
     * @param onlineProfile the online profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerDeath(final PlayerDeathEvent event, final OnlineProfile onlineProfile) throws QuestException {
        checkLocation(onlineProfile, event.getEntity().getLocation());
    }

    /**
     * The onPlayerRespawn method listens for the PlayerRespawnEvent and checks the player's location.
     *
     * @param event         the PlayerRespawnEvent to be used in the method
     * @param onlineProfile the online profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerRespawn(final PlayerRespawnEvent event, final OnlineProfile onlineProfile) throws QuestException {
        checkLocation(onlineProfile, event.getRespawnLocation());
    }

    /**
     * The onPlayerTeleport method listens for the PlayerTeleportEvent and checks the player's location.
     *
     * @param event         the PlayerTeleportEvent to be used in the method
     * @param onlineProfile the online profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerTeleport(final PlayerTeleportEvent event, final OnlineProfile onlineProfile) throws QuestException {
        onPlayerMove(event, onlineProfile);
    }

    /**
     * The onPlayerMove method listens for the PlayerMoveEvent and checks the player's location.
     *
     * @param event         the PlayerMoveEvent to be used in the method
     * @param onlineProfile the online profile of the player
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onPlayerMove(final PlayerMoveEvent event, final OnlineProfile onlineProfile) throws QuestException {
        checkLocation(onlineProfile, event.getTo());
    }

    /**
     * The onVehicleMove method listens for the VehicleMoveEvent and checks the player's location.
     *
     * @param event the VehicleMoveEvent to be used in the method
     * @throws QuestException if argument resolving for the profile fails
     */
    public void onVehicleMove(final VehicleMoveEvent event) throws QuestException {
        final List<Entity> passengers = event.getVehicle().getPassengers();
        for (final Entity passenger : passengers) {
            if (passenger instanceof final Player player) {
                final OnlineProfile onlineProfile = getService().getProfileProvider().getProfile(player);
                if (!getService().checkConditions(onlineProfile) || !getService().containsProfile(onlineProfile)) {
                    continue;
                }
                checkLocation(onlineProfile, event.getTo());
            }
        }
    }

    private void checkLocation(final OnlineProfile onlineProfile, final Location location) throws QuestException {
        final boolean toInside = isInsideHandleException(location, onlineProfile);
        if (!entry.getValue(onlineProfile).orElse(false) && !exit.getValue(onlineProfile).orElse(false)) {
            if (toInside) {
                getService().complete(onlineProfile);
            }
            return;
        }

        checkLocationEnterExit(onlineProfile, toInside);
    }

    private void checkLocationEnterExit(final OnlineProfile onlineProfile, final boolean toInside) throws QuestException {
        if (!playersInsideRegion.containsKey(onlineProfile.getProfileUUID())) {
            playersInsideRegion.put(onlineProfile.getProfileUUID(), toInside);
            return;
        }

        final boolean fromInside = playersInsideRegion.get(onlineProfile.getProfileUUID());
        playersInsideRegion.put(onlineProfile.getProfileUUID(), toInside);

        if (entry.getValue(onlineProfile).orElse(false) && toInside && !fromInside
                || exit.getValue(onlineProfile).orElse(false) && fromInside && !toInside) {
            getService().complete(onlineProfile);
            playersInsideRegion.remove(onlineProfile.getProfileUUID());
        }
    }

    private boolean isInsideHandleException(final Location location, final OnlineProfile onlineProfile) throws QuestException {
        final AtomicBoolean toInsideAtomic = new AtomicBoolean();
        toInsideAtomic.set(isInside(onlineProfile, location));
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
