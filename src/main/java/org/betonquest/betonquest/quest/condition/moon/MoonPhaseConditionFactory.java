package org.betonquest.betonquest.quest.condition.moon;

import io.papermc.paper.world.MoonPhase;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
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
        final Variable<List<MoonPhase>> moonPhases = instruction.getList(Argument.ENUM(MoonPhase.class));
        final Variable<World> world = instruction.get(instruction.getValue("world", "%location.world%"),
                Argument.WORLD);
        return new PrimaryServerThreadPlayerCondition(
                new NullableConditionAdapter(new MoonPhasesCondition(world, moonPhases)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        final Variable<World> world = instruction.getValue("world", Argument.WORLD);
        if (world == null) {
            return new ThrowExceptionPlayerlessCondition();
        }
        final Variable<List<MoonPhase>> moonPhases = instruction.getList(Argument.ENUM(MoonPhase.class));
        return new PrimaryServerThreadPlayerlessCondition(
                new NullableConditionAdapter(new MoonPhasesCondition(world, moonPhases)), data);
    }
}
