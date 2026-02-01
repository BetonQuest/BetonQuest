package org.betonquest.betonquest.quest.action.explosion;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.NullableAction;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns an explosion in a given location and with given stats.
 */
public class ExplosionAction implements NullableAction {

    /**
     * The location of the explosion.
     */
    private final Argument<Location> location;

    /**
     * The power of the explosion.
     */
    private final Argument<Number> power;

    /**
     * Whether the explosion should set fire.
     */
    private final Argument<Boolean> setsFire;

    /**
     * Whether the explosion should break blocks.
     */
    private final Argument<Boolean> breaksBlocks;

    /**
     * Creates a new explosion action.
     *
     * @param location     the location of the explosion
     * @param power        the power of the explosion
     * @param setsFire     whether the explosion should set fire
     * @param breaksBlocks whether the explosion should break blocks
     */
    public ExplosionAction(final Argument<Location> location, final Argument<Number> power, final Argument<Boolean> setsFire, final Argument<Boolean> breaksBlocks) {
        this.location = location;
        this.power = power;
        this.setsFire = setsFire;
        this.breaksBlocks = breaksBlocks;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location resolvedLocation = location.getValue(profile);
        resolvedLocation.getWorld().createExplosion(resolvedLocation,
                power.getValue(profile).floatValue(), setsFire.getValue(profile), breaksBlocks.getValue(profile));
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
