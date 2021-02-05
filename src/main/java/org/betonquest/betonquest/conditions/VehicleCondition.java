package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class VehicleCondition extends Condition {

    private EntityType vehicle;
    private boolean any;

    public VehicleCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String name = instruction.next();
        if ("any".equalsIgnoreCase(name)) {
            any = true;
        } else {
            try {
                vehicle = EntityType.valueOf(name.toUpperCase(Locale.ROOT));
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
