/*
 * Created on 29.06.2018.
 */
package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

/**
 * Kills all mobs of given type at location.
 * <p>
 * Created on 29.06.2018.
 */
@SuppressWarnings("PMD.CommentRequired")
public class KillMobEvent extends QuestEvent {

    private final EntityType type;
    private final CompoundLocation loc;
    private final VariableNumber radius;
    private String name;
    private String marked;


    public KillMobEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        type = instruction.getEnum(EntityType.class);
        loc = instruction.getLocation();
        radius = instruction.getVarNum();
        name = instruction.getOptional("name");
        if (name != null) {
            name = Utils.format(name, true, false).replace('_', ' ');
        }
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        final double radiusSquared = this.radius.getDouble(playerID) * this.radius.getDouble(playerID);
        location
                .getWorld()
                .getEntitiesByClass(type.getEntityClass())
                .stream()
                //get only nearby entities
                .filter(entity -> entity.getLocation().distanceSquared(location) <= radiusSquared)
                //only entities with given name
                .filter(entity -> {
                    if (name == null) {
                        return true;
                    }
                    return name.equals(entity.getName());
                })
                //only entities marked
                .filter(entity -> {
                    if (marked == null) {
                        return true;
                    }
                    return entity
                            .getMetadata("betonquest-marked")
                            .stream()
                            .anyMatch(metadataValue -> metadataValue.asString().equals(marked));
                })
                //remove them
                .forEach(Entity::remove);
        return null;
    }
}
