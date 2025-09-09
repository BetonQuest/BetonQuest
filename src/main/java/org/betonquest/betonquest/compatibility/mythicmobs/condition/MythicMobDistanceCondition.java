package org.betonquest.betonquest.compatibility.mythicmobs.condition;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.entity.Player;

/**
 * Condition that checks if a player is within a certain distance of a specific MythicMob.
 */
public class MythicMobDistanceCondition implements OnlineCondition {
    /**
     * The BukkitAPIHelper used to interact with MythicMobs.
     */
    private final BukkitAPIHelper apiHelper;

    /**
     * The internal name of the MythicMob to check for.
     */
    private final String mythicMobInternalName;

    /**
     * The distance within which the MythicMob should be checked.
     */
    private final Variable<Number> distance;

    /**
     * Constructs a new MythicMobDistanceCondition.
     *
     * @param apiHelper             the BukkitAPIHelper to use for checking MythicMobs
     * @param mythicMobInternalName the internal name of the MythicMob to check for
     * @param distance              the distance within which the MythicMob should be checked
     */
    public MythicMobDistanceCondition(final BukkitAPIHelper apiHelper, final String mythicMobInternalName, final Variable<Number> distance) {
        this.apiHelper = apiHelper;
        this.mythicMobInternalName = mythicMobInternalName;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final double dist = distance.getValue(profile).doubleValue();

        return player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist)
                .stream().anyMatch(entity -> entity != null
                        && apiHelper.isMythicMob(entity)
                        && apiHelper.getMythicMobInstance(entity).getType().getInternalName().equals(mythicMobInternalName));
    }
}
