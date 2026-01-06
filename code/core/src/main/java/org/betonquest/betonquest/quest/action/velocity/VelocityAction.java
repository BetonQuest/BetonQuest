package org.betonquest.betonquest.quest.action.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * The velocity action. Throws the player around.
 */
public class VelocityAction implements OnlineAction {

    /**
     * The vector of the direction and speed.
     */
    private final Argument<Vector> vector;

    /**
     * Sets in which direction the vector is directed.
     */
    private final Argument<VectorDirection> direction;

    /**
     * Sets how the vector should get merged with the player-velocity.
     */
    private final Argument<VectorModification> modification;

    /**
     * Create a velocity action with the given parameters.
     *
     * @param vector       vector of the direction and speed
     * @param direction    direction in which the vector is directed
     * @param modification modification how the vector should get merged with the player-velocity
     */
    public VelocityAction(final Argument<Vector> vector, final Argument<VectorDirection> direction,
                          final Argument<VectorModification> modification) {
        this.vector = vector;
        this.direction = direction;
        this.modification = modification;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final Vector pVector = vector.getValue(profile);
        final Vector directionVector = direction.getValue(profile).calculate(player, pVector);
        final Vector modificationVector = modification.getValue(profile).calculate(player, directionVector);
        player.setVelocity(modificationVector);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
