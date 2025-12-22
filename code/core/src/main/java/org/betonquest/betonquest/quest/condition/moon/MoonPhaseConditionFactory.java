package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.condition.ThrowExceptionPlayerlessCondition;
import org.bukkit.World;

import java.util.List;

/**
 * Factory to create moon phase conditions from {@link Instruction}s.
 */
public class MoonPhaseConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the moon phase condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public MoonPhaseConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<List<MoonPhase>> moonPhases = instruction.getList(instruction.getParsers().forEnum(MoonPhase.class));
        final Variable<World> world = instruction.get(instruction.getValue("world", "%location.world%"),
                instruction.getParsers().world());
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new MoonPhasesCondition(world, moonPhases)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<World> world = instruction.getValue("world", instruction.getParsers().world());
        if (world == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<List<MoonPhase>> moonPhases = instruction.getList(instruction.getParsers().forEnum(MoonPhase.class));
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new MoonPhasesCondition(world, moonPhases)), data);
    }
}
