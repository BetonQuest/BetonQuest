package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * The velocity event. Throws the player around.
 */
public class VelocityEvent implements OnlineEvent {

    /**
     * The vector of the direction and speed.
     */
    private final Variable<Vector> vector;

    /**
     * Sets in which direction the vector is directed.
     */
    private final Variable<VectorDirection> direction;

    /**
     * Sets how the vector should get merged with the player-velocity.
     */
    private final Variable<VectorModification> modification;

    /**
     * Create a velocity event with the given parameters.
     *
     * @param vector       vector of the direction and speed
     * @param direction    direction in which the vector is directed
     * @param modification modification how the vector should get merged with the player-velocity
     */
    public VelocityEvent(final Variable<Vector> vector, final Variable<VectorDirection> direction,
                         final Variable<VectorModification> modification) {
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
