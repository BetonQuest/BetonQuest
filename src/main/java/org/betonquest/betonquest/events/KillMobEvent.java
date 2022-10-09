package org.betonquest.betonquest.events;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
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
    private final String name;
    private final String marked;


    public KillMobEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        type = instruction.getEnum(EntityType.class);
        loc = instruction.getLocation();
        radius = instruction.getVarNum();
        final String nameStaring = instruction.getOptional("name");
        name = nameStaring == null ? null : Utils.format(nameStaring, true, false).replace('_', ' ');

        final String markedString = instruction.getOptional("marked");
        marked = markedString == null ? null : Utils.addPackage(instruction.getPackage(), markedString);
    }

    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        final Location location = loc.getLocation(profile);
        final double radiusSquared = this.radius.getDouble(profile) * this.radius.getDouble(profile);
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
                            .anyMatch(metadataValue -> metadataValue.asString().equals(marked.replace("%player%", profile.getProfileUUID().toString())));
                })
                //remove them
                .forEach(Entity::remove);
        return null;
    }
}
