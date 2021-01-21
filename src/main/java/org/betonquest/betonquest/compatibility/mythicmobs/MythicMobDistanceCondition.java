package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class MythicMobDistanceCondition extends Condition {

    private final String mythicMobInternalName;

    private final VariableNumber distance;

    private final BukkitAPIHelper apiHelper;

    public MythicMobDistanceCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        mythicMobInternalName = instruction.next();
        apiHelper = new BukkitAPIHelper();

        if (apiHelper.getMythicMob(mythicMobInternalName) == null) {
            throw new InstructionParseException("MythicMob with internal name '" + mythicMobInternalName + "' does not exist");
        }

        distance = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final double dist = distance.getDouble(playerID);

        return player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist)
                .stream().anyMatch(entity -> entity != null
                        && apiHelper.isMythicMob(entity)
                        && apiHelper.getMythicMobInstance(entity).getType().getInternalName().equals(mythicMobInternalName));
    }

}
