package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

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
            } catch (final InvalidMobTypeException e) {
                throw new QuestRuntimeException("MythicMob type " + mob + " is invalid.", e);
            }
        }
        return null;
    }

}
