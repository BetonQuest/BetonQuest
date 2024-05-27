package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@SuppressWarnings("PMD.CommentRequired")
public class RideCondition extends Condition {
    /**
     * The string to match any entity.
     */
    private static final String ANY_ENTITY = "any";

    private final boolean any;

    @Nullable
    private EntityType vehicle;

    public RideCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String name = instruction.next();
        if (ANY_ENTITY.equalsIgnoreCase(name)) {
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

    @Override
    protected Boolean execute(final Profile profile) {
        final Entity entity = profile.getOnlineProfile().get().getPlayer().getVehicle();
        return entity != null && (any || entity.getType() == vehicle);
    }

}
