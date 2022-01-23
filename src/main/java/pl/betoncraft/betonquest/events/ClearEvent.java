package pl.betoncraft.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.Utils;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Clears all specified monsters in a certain location
 */
@SuppressWarnings("PMD.CommentRequired")
public class ClearEvent extends QuestEvent {

    private final EntityType[] types;
    private final CompoundLocation loc;
    private final VariableNumber range;
    private final String name;
    private final boolean kill;
    private String marked;

    public ClearEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        final String[] entities = instruction.getArray();
        types = new EntityType[entities.length];
        for (int i = 0; i < types.length; i++) {
            try {
                types[i] = EntityType.valueOf(entities[i].toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException e) {
                throw new InstructionParseException("Entity type '" + entities[i] + "' does not exist", e);
            }
        }
        loc = instruction.getLocation();
        range = instruction.getVarNum();
        name = instruction.getOptional("name");
        kill = instruction.hasArgument("kill");
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        final Collection<Entity> entities = location.getWorld().getEntities();
        loop:
        for (final Entity entity : entities) {
            if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
                continue;
            }
            if (marked != null) {
                if (!entity.hasMetadata("betonquest-marked")) {
                    continue;
                }
                final List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
                for (final MetadataValue m : meta) {
                    if (!m.asString().equals(marked)) {
                        continue loop;
                    }
                }
            }
            final double range = this.range.getDouble(playerID);
            if (entity.getLocation().distanceSquared(location) < range * range) {
                final EntityType entityType = entity.getType();
                for (final EntityType allowedType : types) {
                    if (entityType == allowedType) {
                        if (kill) {
                            final LivingEntity living = (LivingEntity) entity;
                            living.damage(living.getHealth() + 10);
                        } else {
                            entity.remove();
                        }
                        break;
                    }
                }
            }
        }
        return null;
    }

}
