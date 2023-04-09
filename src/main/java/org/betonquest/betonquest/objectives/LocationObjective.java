package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.BetonQuestLogger;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import java.util.List;

/**
 * Player has to reach certain radius around the specified location
 */
@SuppressWarnings("PMD.CommentRequired")
public class LocationObjective extends Objective implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuestLogger.create(LocationObjective.class);

    private final CompoundLocation loc;
    private final VariableNumber range;

    public LocationObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
        range = instruction.getVarNum();
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(final PlayerMoveEvent event) {
        qreHandler.handle(() -> {
            checkLocation(event.getTo(), PlayerConverter.getID(event.getPlayer()));
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onRide(final VehicleMoveEvent event) {
        qreHandler.handle(() -> {
            final List<Entity> passengers = event.getVehicle().getPassengers();
            for (final Entity passenger : passengers) {
                if (passenger instanceof final Player player) {
                    checkLocation(event.getTo(), PlayerConverter.getID(player));
                }
            }
        });
    }

    private void checkLocation(final Location toLocation, final Profile profile) throws QuestRuntimeException {
        final Location location = loc.getLocation(profile);
        if (containsPlayer(profile) && toLocation.getWorld().equals(location.getWorld())) {
            final double pRange = range.getDouble(profile);
            if (toLocation.distanceSquared(location) <= pRange * pRange && super.checkConditions(profile)) {
                completeObjective(profile);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        if ("location".equalsIgnoreCase(name)) {
            final Location location;
            try {
                location = loc.getLocation(profile);
            } catch (final QuestRuntimeException e) {
                LOG.warn(instruction.getPackage(), "Error while getting location property in '" + instruction.getID() + "' objective: "
                        + e.getMessage(), e);
                return "";
            }
            return "X: " + location.getBlockX() + ", Y: " + location.getBlockY() + ", Z: " + location.getBlockZ();
        }
        return "";
    }

}
