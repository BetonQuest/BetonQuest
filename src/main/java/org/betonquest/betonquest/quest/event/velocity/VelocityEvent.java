package org.betonquest.betonquest.quest.event.velocity;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.instruction.variable.location.VariableVector;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * The velocity event. Throws the player around.
 */
public class VelocityEvent implements OnlineEvent {
    /**
     * The vector of the direction and speed.
     */
    private final VariableVector vector;

    /**
     * Sets in which direction the vector is directed.
     */
    private final VectorDirection direction;

    /**
     * Sets how the vector should get merged with the player-velocity.
     */
    private final VectorModification modification;

    /**
     * Create a velocity event with the given parameters.
     *
     * @param vector       vector of the direction and speed
     * @param direction    direction in which the vector is directed
     * @param modification modification how the vector should get merged with the player-velocity
     */
    public VelocityEvent(final VariableVector vector, final VectorDirection direction, final VectorModification modification) {
        this.vector = vector;
        this.direction = direction;
        this.modification = modification;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final Vector pVector = vector.getValue(profile);
        final Vector directionVector = direction.calculate(player, pVector);
        final Vector modificationVector = modification.calculate(player, directionVector);
        player.setVelocity(modificationVector);
    }
}
