package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Spawns an explosion in a given location and with given stats.
 */
public class ExplosionEvent implements NullableEvent {
    /**
     * The location of the explosion.
     */
    private final VariableLocation location;

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
    public ExplosionEvent(final VariableLocation location, final VariableNumber power, final boolean setsFire, final boolean breaksBlocks) {
        this.location = location;
        this.power = power;
        this.setsFire = setsFire;
        this.breaksBlocks = breaksBlocks;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location resolvedLocation = location.getValue(profile);
        resolvedLocation.getWorld().createExplosion(resolvedLocation,
                power.getValue(profile).floatValue(), setsFire, breaksBlocks);
    }
}
