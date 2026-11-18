package org.betonquest.betonquest.quest.action.projectile;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.Optional;

/**
 * Factory to create {@link ProjectileAction}s from {@link Instruction}s.
 */
public class ProjectileActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create a new projectile action factory.
     */
    public ProjectileActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    private NullableActionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<EntityType> type = instruction.enumeration(EntityType.class)
                .validate(entity -> entity.getEntityClass() != null && Projectile.class.isAssignableFrom(entity.getEntityClass()), "'%s' is not a projectile.")
                .get();

        final Argument<Location> origin = instruction.location().get();
        final Argument<Vector> direction = instruction.vector().get();
        final Optional<Argument<Number>> speed = instruction.number().get("speed");
        final Argument<Boolean> gravity = instruction.bool().get("gravity", true);

        final ProjectileAction projectileAction = new ProjectileAction(type, origin, direction, speed.orElse(null), gravity);
        return new NullableActionAdapter(projectileAction);
    }
}
