package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A condition that checks the moon phase in the given world.
 */
public class MoonPhaseCondition implements NullableCondition {

    /**
     * The world to check the moon phase in.
     */
    private final Argument<World> world;

    /**
     * The moon phases to check for.
     */
    private final Argument<List<MoonPhase>> moonPhases;

    /**
     * Checks if the moon phase in the given world matches one of the moon phases of this condition.
     *
     * @param world      the world to check the moon phase in
     * @param moonPhases the moon phases to check for
     */
    public MoonPhaseCondition(final Argument<World> world, final Argument<List<MoonPhase>> moonPhases) {
        this.world = world;
        this.moonPhases = moonPhases;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<MoonPhase> moonPhases = this.moonPhases.getValue(profile);
        return moonPhases.contains(world.getValue(profile).getMoonPhase());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
