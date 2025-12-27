package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * Strikes a lightning at specified location.
 */
public class LightningEvent implements NullableEvent {

    /**
     * The location to strike the lightning at.
     */
    private final Argument<Location> location;

    /**
     * Whether the lightning should do damage.
     */
    private final FlagArgument<Boolean> noDamage;

    /**
     * Creates a new lightning event.
     *
     * @param location the location to strike the lightning at
     * @param noDamage whether the lightning should do damage
     */
    public LightningEvent(final Argument<Location> location, final FlagArgument<Boolean> noDamage) {
        this.location = location;
        this.noDamage = noDamage;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final Location loc = location.getValue(profile);
        final World world = loc.getWorld();
        if (noDamage.getValue(profile).orElse(false)) {
            world.strikeLightningEffect(loc);
        } else {
            world.strikeLightning(loc);
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
