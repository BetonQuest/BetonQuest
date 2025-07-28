package org.betonquest.betonquest.compatibility.mythicmobs.condition;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.entity.Player;

/**
 * Condition that checks if a player is within a certain distance of a specific MythicMob.
 */
@SuppressWarnings("PMD.CommentRequired")
public class MythicMobDistanceCondition implements PlayerCondition {
    private final BukkitAPIHelper apiHelper;

    private final String mythicMobInternalName;

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
    public boolean check(final Profile profile) throws QuestException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        final double dist = distance.getValue(profile).doubleValue();

        return player.getWorld().getNearbyEntities(player.getLocation(), dist, dist, dist)
                .stream().anyMatch(entity -> entity != null
                        && apiHelper.isMythicMob(entity)
                        && apiHelper.getMythicMobInstance(entity).getType().getInternalName().equals(mythicMobInternalName));
    }
}
