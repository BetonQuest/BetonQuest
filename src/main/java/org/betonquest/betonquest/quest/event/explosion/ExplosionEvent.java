package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;

/**
 * Spawns an explosion in a given location and with given stats.
 */
public class ExplosionEvent implements Event {
    /**
     * The location of the explosion.
     */
    private final CompoundLocation location;

    /**
     * The power of the explosion.
     */
    private final VariableNumber power;

    /**
     * Whether the explosion should set fire.
     */
    private final boolean setsFire;

    /**
     * Whether the explosion should break blocks.
     */
    private final boolean breaksBlocks;

    /**
     * Creates a new explosion event.
     *
     * @param location     the location of the explosion
     * @param power        the power of the explosion
     * @param setsFire     whether the explosion should set fire
     * @param breaksBlocks whether the explosion should break blocks
     */
    public ExplosionEvent(final CompoundLocation location, final VariableNumber power, final boolean setsFire, final boolean breaksBlocks) {
        this.location = location;
        this.power = power;
        this.setsFire = setsFire;
        this.breaksBlocks = breaksBlocks;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Location resolvedLocation = location.getLocation(profile);
        resolvedLocation.getWorld().createExplosion(resolvedLocation,
                (float) power.getDouble(profile), setsFire, breaksBlocks);
    }
}
