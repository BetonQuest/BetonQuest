package org.betonquest.betonquest.quest.action.projectile;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

/**
 * The projectile action.
 */
public class ProjectileAction implements NullableAction {

    /**
     * The type of the projectile.
     */
    private final Argument<EntityType> type;

    /**
     * The location from which the projectile should be spawned.
     */
    private final Argument<Location> origin;

    /**
     * The direction in which the projectile should be launched.
     */
    private final Argument<Vector> direction;

    /**
     * The speed multiplier for the projectile.
     */
    @Nullable
    private final Argument<Number> speed;

    /**
     * Whether the projectile should be affected by gravity.
     */
    private final Argument<Boolean> gravity;

    /**
     * Create a new projectile action.
     *
     * @param type      the projectile type
     * @param origin    the origin location
     * @param direction the direction vector
     * @param speed     the speed multiplier
     * @param gravity   if gravity should be enabled
     */
    public ProjectileAction(final Argument<EntityType> type, final Argument<Location> origin,
                            final Argument<Vector> direction, @Nullable final Argument<Number> speed,
                            final Argument<Boolean> gravity) {
        this.type = type;
        this.origin = origin;
        this.direction = direction;
        this.speed = speed;
        this.gravity = gravity;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final EntityType pType = type.getValue(profile);
        final ProjectileSource source = profile instanceof final OnlineProfile online ? online.getPlayer() : null;

        final Location loc = origin.getValue(profile);
        final Vector dir = direction.getValue(profile);
        final boolean gravityEnabled = gravity.getValue(profile);

        Vector velocity = dir;
        if (speed != null) {
            final double velMultiplier = speed.getValue(profile).doubleValue();
            velocity = dir.clone().normalize().multiply(velMultiplier);
        }

        final Entity entity = loc.getWorld().spawnEntity(loc, pType);
        if (!(entity instanceof final Projectile projectile)) {
            entity.remove();
            throw new QuestException("Entity is not a projectile: " + pType);
        }

        if (source != null) {
            projectile.setShooter(source);
        }

        projectile.setVelocity(velocity);
        if (projectile instanceof final Fireball fireball) {
            fireball.setDirection(velocity);
        }

        projectile.setGravity(gravityEnabled);
    }
}
