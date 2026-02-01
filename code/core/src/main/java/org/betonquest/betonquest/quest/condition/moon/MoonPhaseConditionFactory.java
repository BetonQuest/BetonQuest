package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.bukkit.World;

import java.util.List;

/**
 * Factory to create moon phase conditions from {@link Instruction}s.
 */
public class MoonPhaseConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the moon phase condition factory.
     */
    public MoonPhaseConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<MoonPhase>> moonPhases = instruction.enumeration(MoonPhase.class).list().get();
        final String worldRaw = instruction.string().get("world", "%location.world%").getValue(null);
        final Argument<World> world = instruction.chainForArgument(worldRaw).world().get();
        return new NullableConditionAdapter(new MoonPhaseCondition(world, moonPhases));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Argument<World> world = instruction.world().get("world").orElse(null);
        if (world == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Argument<List<MoonPhase>> moonPhases = instruction.enumeration(MoonPhase.class).list().get();
        return new NullableConditionAdapter(new MoonPhaseCondition(world, moonPhases));
    }
}
