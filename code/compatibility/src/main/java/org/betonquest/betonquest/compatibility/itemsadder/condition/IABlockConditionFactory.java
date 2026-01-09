package org.betonquest.betonquest.compatibility.itemsadder.condition;

import dev.lone.itemsadder.api.CustomStack;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.compatibility.itemsadder.ItemsAdderParser;
import org.bukkit.Location;

/**
 * Factory to create {@link IABlockCondition}s from {@link Instruction}s.
 */
public class IABlockConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The empty default constructor.
     */
    public IABlockConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseInstruction(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseInstruction(instruction));
    }

    private IABlockCondition parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<CustomStack> itemID = instruction.parse(ItemsAdderParser.ITEMS_ADDER_PARSER).get();
        final Argument<Location> location = instruction.location().get();
        return new IABlockCondition(itemID, location);
    }
}
