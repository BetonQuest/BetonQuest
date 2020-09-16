package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.Locale;

public class VehicleObjective extends Objective implements Listener {

    private EntityType vehicle;
    private boolean any;

    public VehicleObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        final String name = instruction.next();
        if (name.equalsIgnoreCase("any")) {
            any = true;
        } else {
            try {
                vehicle = EntityType.valueOf(name.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Entity type " + name + " does not exist.", e);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }
        final String playerID = PlayerConverter.getID((Player) event.getEntered());
        if (containsPlayer(playerID) && (any || event.getVehicle().getType() == vehicle) && checkConditions(playerID)) {
            completeObjective(playerID);
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
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
