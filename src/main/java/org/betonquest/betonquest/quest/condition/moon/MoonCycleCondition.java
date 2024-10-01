package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableWorld;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A condition that checks the moon cycle in the given world.
 */
public class MoonCycleCondition implements NullableCondition {

    /**
     * The world to check the moon cycle in.
     */
    private final VariableWorld variableWorld;

    /**
     * The moon cycle to check for.
     */
    private final VariableNumber moonCycle;

    /**
     * The mapping of moon cycle keys to moon cycle names.
     */
    private final Map<Integer, MoonPhase> moonCycleNames = Map.of(
            1, MoonPhase.FULL_MOON,
            2, MoonPhase.WANING_GIBBOUS,
            3, MoonPhase.LAST_QUARTER,
            4, MoonPhase.WANING_CRESCENT,
            5, MoonPhase.NEW_MOON,
            6, MoonPhase.WAXING_CRESCENT,
            7, MoonPhase.FIRST_QUARTER,
            8, MoonPhase.WAXING_GIBBOUS
    );

    /**
     * Checks if the moon cycle in the given world matches the moon cycle of this condition.
     *
     * @param variableWorld the world to check the moon cycle in
     * @param moonCycle     the moon cycle to check for
     */
    public MoonCycleCondition(final VariableWorld variableWorld, final VariableNumber moonCycle) {
        this.variableWorld = variableWorld;
        this.moonCycle = moonCycle;
    }

    @Override
    public boolean check(@Nullable final Profile profile) throws QuestRuntimeException {
        final World world = variableWorld.getValue(profile);
        final int moonCycleKey = moonCycle.getValue(profile).intValue();
        if (moonCycleKey < 1 || moonCycleKey > 8) {
            throw new QuestRuntimeException("Invalid moon cycle key: " + moonCycleKey);
        }
        return world.getMoonPhase().equals(moonCycleNames.get(moonCycleKey));
    }
}
