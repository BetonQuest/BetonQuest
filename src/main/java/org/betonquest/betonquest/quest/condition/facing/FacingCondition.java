package org.betonquest.betonquest.quest.condition.facing;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.bukkit.Location;

/**
 * Requires the player to be facing a specified direction.
 */
public class FacingCondition implements OnlineCondition {

    /**
     * The direction the player should be facing.
     */
    private final Direction direction;

    /**
     * Creates a new facing condition.
     *
     * @param direction The direction the player should be facing
     */
    public FacingCondition(final Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final Location playerLocation = profile.getPlayer().getLocation();
        final Direction playerDirection = Direction.parseDirection(playerLocation.getYaw(), playerLocation.getPitch());
        return direction == playerDirection;
    }
}
