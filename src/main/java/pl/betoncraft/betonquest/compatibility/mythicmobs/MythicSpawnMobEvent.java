package pl.betoncraft.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.bukkit.Location;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.location.CompoundLocation;

/**
 * Spawns MythicMobs mobs
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicSpawnMobEvent extends QuestEvent {

    private final CompoundLocation loc;
    private final String mob;
    private final VariableNumber amount;
    private final VariableNumber level;

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public MythicSpawnMobEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        loc = instruction.getLocation();
        final String[] mobParts = instruction.next().split(":");
        if (mobParts.length != 2) {
            throw new InstructionParseException("Wrong mob format");
        }
        mob = mobParts[0];
        level = instruction.getVarNum(mobParts[1]);
        amount = instruction.getVarNum();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final int pAmount = amount.getInt(playerID);
        final int level = this.level.getInt(playerID);
        final Location location = loc.getLocation(playerID);
        for (int i = 0; i < pAmount; i++) {
            try {
                new BukkitAPIHelper().spawnMythicMob(mob, location, level);
            } catch (InvalidMobTypeException e) {
                throw new QuestRuntimeException("MythicMob type " + mob + " is invalid.", e);
            }
        }
        return null;
    }

}
