package pl.betoncraft.betonquest.conditions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class VehicleCondition extends Condition {

    private EntityType vehicle;
    private boolean any;

    public VehicleCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String name = instruction.next();
        if (name.equalsIgnoreCase("any")) {
            any = true;
        } else {
            try {
                vehicle = EntityType.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Entity type " + name + " does not exist.", e);
            }
        }
    }

    @Override
    protected Boolean execute(final String playerID) {
        final Entity entity = PlayerConverter.getPlayer(playerID).getVehicle();
        return entity != null && (any || entity.getType() == vehicle);
    }

}
