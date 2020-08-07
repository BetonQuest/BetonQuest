package pl.betoncraft.betonquest.compatibility.mythicmobs;

import java.util.Collection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;

public class MythicMobDistanceCondition extends Condition {

    private String mythicMobInternalName;

    private VariableNumber distance;

    private BukkitAPIHelper apiHelper;

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

        final Collection<Entity> entities = player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist);
        for (final Entity entity : entities) {
            if (entity == null) {
                continue;
            }
            if (!apiHelper.isMythicMob(entity)) {
                continue;
            }
            if (!apiHelper.getMythicMobInstance(entity).getType().getInternalName().equals(mythicMobInternalName)) {
                continue;
            }
            return true;
        }

        return false;
    }

}
