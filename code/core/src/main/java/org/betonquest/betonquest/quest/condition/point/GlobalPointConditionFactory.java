package org.betonquest.betonquest.quest.condition.point;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.DefaultInstruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory to create global point conditions from {@link DefaultInstruction}s.
 */
public class GlobalPointConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates the global point factory.
     *
     * @param globalData the global data
     */
    public GlobalPointConditionFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public PlayerCondition parsePlayer(final DefaultInstruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final DefaultInstruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private GlobalPointCondition parse(final DefaultInstruction instruction) throws QuestException {
        final Variable<String> category = instruction.get(PackageArgument.IDENTIFIER);
        final Variable<Number> count = instruction.get(Argument.NUMBER);
        final boolean equal = instruction.hasArgument("equal");
        return new GlobalPointCondition(globalData, category, count, equal);
    }
}
