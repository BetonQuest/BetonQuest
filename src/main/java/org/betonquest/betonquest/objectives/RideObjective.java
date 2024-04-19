package org.betonquest.betonquest.objectives;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class RideObjective extends Objective implements Listener {
    private final boolean any;

    private EntityType vehicle;

    public RideObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        final String name = instruction.next();
        if ("any".equalsIgnoreCase(name)) {
            any = true;
        } else {
            any = false;
            try {
                vehicle = EntityType.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Entity type " + name + " does not exist.", e);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }
        final OnlineProfile onlineProfile = PlayerConverter.getID((Player) event.getEntered());
        if (containsPlayer(onlineProfile) && (any || event.getVehicle().getType() == vehicle) && checkConditions(onlineProfile)) {
            completeObjective(onlineProfile);
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
        return "";
    }

}
