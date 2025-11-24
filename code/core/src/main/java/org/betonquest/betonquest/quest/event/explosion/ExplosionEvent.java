package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns an explosion in a given location and with given stats.
 */
public class ExplosionEvent implements NullableEvent {
    /**
     * The location of the explosion.
     */
    private final Variable<Location> location;

    /**
     * The power of the explosion.
     */
    private final Variable<Number> power;

    /**
     * Whether the explosion should set fire.
     */
    private final Variable<Boolean> setsFire;

    /**
     * Whether the explosion should break blocks.
     */
    private final Variable<Boolean> breaksBlocks;

    /**
     * Creates a new explosion event.
     *
     * @param location     the location of the explosion
     * @param power        the power of the explosion
     * @param setsFire     whether the explosion should set fire
     * @param breaksBlocks whether the explosion should break blocks
     */
    public ExplosionEvent(final Variable<Location> location, final Variable<Number> power, final Variable<Boolean> setsFire, final Variable<Boolean> breaksBlocks) {
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
}
