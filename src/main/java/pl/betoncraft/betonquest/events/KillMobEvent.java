/**
 * Created on 29.06.2018.
 *
 * @author Jonas Blocher
 */
package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Kills all mobs of given type at location.
 * <p>
 * Created on 29.06.2018.
 *
 * @author Jonas Blocher
 */
public class KillMobEvent extends QuestEvent {

    private EntityType type;
    private LocationData loc;
    private VariableNumber radius;
    private String name;
    private String marked;


    public KillMobEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
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
    public void run(String playerID) throws QuestRuntimeException {
        Location location = loc.getLocation(playerID);
        final double radius_squared = this.radius.getDouble(playerID) * this.radius.getDouble(playerID);
        location
                .getWorld()
                .getEntitiesByClass(type.getEntityClass())
                .stream()
                //get only nearby entities
                .filter(entity -> entity.getLocation().distanceSquared(location) <= radius_squared)
                //only entities with given name
                .filter(entity -> {
                    if (name == null) return true;
                    return name.equals(entity.getName());
                })
                //only entities marked
                .filter(entity -> {
                    if (marked == null) return true;
                    return entity
                            .getMetadata("betonquest-marked")
                            .stream()
                            .anyMatch(metadataValue -> metadataValue.asString().equals(marked));
                })
                //remove them
                .forEach(Entity::remove);
    }
}