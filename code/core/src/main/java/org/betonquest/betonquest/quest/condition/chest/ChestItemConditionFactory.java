package org.betonquest.betonquest.quest.condition.chest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.Item;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.bukkit.Location;

import java.util.List;

/**
 * Factory to create chest item conditions from {@link Instruction}s.
 */
public class ChestItemConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the chest item condition factory.
     */
    public ChestItemConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parse(instruction));
    }

    private ChestItemCondition parse(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.get(instruction.getParsers().location());
        final Variable<List<Item>> items = instruction.getList(InstructionIdentifierArgument.ITEM);
        return new ChestItemCondition(loc, items);
    }
}
