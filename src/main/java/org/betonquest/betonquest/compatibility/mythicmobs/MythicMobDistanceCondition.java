package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final double dist = distance.getDouble(profile);

        return player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist)
                .stream().anyMatch(entity -> entity != null
                        && apiHelper.isMythicMob(entity)
                        && apiHelper.getMythicMobInstance(entity).getType().getInternalName().equals(mythicMobInternalName));
    }

}
