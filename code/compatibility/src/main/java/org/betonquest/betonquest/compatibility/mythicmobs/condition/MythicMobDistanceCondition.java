package org.betonquest.betonquest.compatibility.mythicmobs.condition;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;
import org.bukkit.entity.Player;

/**
 * Condition that checks if a player is within a certain distance of a specific MythicMob.
 */
public class MythicMobDistanceCondition implements OnlineCondition {

    /**
     * The Mob Executor used to get MythicMobs.
     */
    private final MobExecutor mobExecutor;

    /**
     * The internal name of the MythicMob to check for.
     */
    private final Argument<MythicMob> mobType;

    /**
     * The distance within which the MythicMob should be checked.
     */
    private final Argument<Number> distance;

    /**
     * Constructs a new MythicMobDistanceCondition.
     *
     * @param mobExecutor the mob executor used to get MythicMobs
     * @param mobType     the MythicMob to check for
     * @param distance    the distance within which the MythicMob should be checked
     */
    public MythicMobDistanceCondition(final MobExecutor mobExecutor, final Argument<MythicMob> mobType, final Argument<Number> distance) {
        this.mobExecutor = mobExecutor;
        this.mobType = mobType;
        this.distance = distance;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final double dist = distance.getValue(profile).doubleValue();
        final MythicMob mob = mobType.getValue(profile);
        return player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist)
                .stream().anyMatch(entity -> {
                    final ActiveMob activeMob = mobExecutor.getMythicMobInstance(entity);
                    return activeMob != null && activeMob.getType().equals(mob);
                });
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
