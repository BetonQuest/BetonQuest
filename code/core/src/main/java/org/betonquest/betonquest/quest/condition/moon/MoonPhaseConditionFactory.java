package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
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
        final Variable<List<MoonPhase>> moonPhases = instruction.enumeration(MoonPhase.class).getList();
        final String worldRaw = instruction.string().get("world", "%location.world%").getValue(null);
        final Variable<World> world = instruction.get(worldRaw, instruction.getParsers().world());
        return new NullableConditionAdapter(new MoonPhaseCondition(world, moonPhases));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<World> world = instruction.world().get("world").orElse(null);
        if (world == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<List<MoonPhase>> moonPhases = instruction.enumeration(MoonPhase.class).getList();
        return new NullableConditionAdapter(new MoonPhaseCondition(world, moonPhases));
    }
}
