package org.betonquest.betonquest.compatibility.mythicmobs.conditions;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class MythicMobDistanceCondition implements PlayerCondition {
    private final BukkitAPIHelper apiHelper;

    private final String mythicMobInternalName;

    private final VariableNumber distance;

    public MythicMobDistanceCondition(final BukkitAPIHelper apiHelper, final String mythicMobInternalName, final VariableNumber distance) throws QuestException {
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
