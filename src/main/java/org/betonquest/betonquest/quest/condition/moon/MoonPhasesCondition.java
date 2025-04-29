package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A condition that checks the moon phase in the given world.
 */
public class MoonPhasesCondition implements NullableCondition {

    /**
     * The world to check the moon phase in.
     */
    private final Variable<World> variableWorld;

    /**
     * The moon phases to check for.
     */
    private final Variable<List<MoonPhase>> moonPhases;

    /**
     * Checks if the moon phase in the given world matches one of the moon phases of this condition.
     *
     * @param variableWorld the world to check the moon phase in
     * @param moonPhases    the moon phases to check for
     */
    public MoonPhasesCondition(final Variable<World> variableWorld, final Variable<List<MoonPhase>> moonPhases) {
        this.variableWorld = variableWorld;
        this.moonPhases = moonPhases;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestException {
        final List<MoonPhase> moonPhases = this.moonPhases.getValue(profile);
        return moonPhases.contains(variableWorld.getValue(profile).getMoonPhase());
    }
}
