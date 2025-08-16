package org.betonquest.betonquest.quest.condition.chest;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
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
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest item conditions from {@link Instruction}s.
 */
public class ChestItemConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the chest item condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public ChestItemConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(new NullableConditionAdapter(parse(instruction)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(new NullableConditionAdapter(parse(instruction)), data);
    }

    private ChestItemCondition parse(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<List<Item>> items = instruction.getList(PackageArgument.ITEM);
        return new ChestItemCondition(loc, items);
    }
}
