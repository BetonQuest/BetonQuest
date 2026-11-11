package org.betonquest.betonquest.quest.action.boostelytra;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.Optional;

/**
 * Factory to create boostelytra actions from {@link Instruction}s.
 */
public class BoostElytraActionFactory implements PlayerActionFactory {

    /**
     * Create the boostelytra action factory.
     */
    public BoostElytraActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Optional<Argument<ItemWrapper>> boostItem = instruction.item().get("item");
        final Optional<Argument<Number>> power = instruction.number().inRange(0, 128).get("power");
        return new OnlineActionAdapter(new BoostElytraAction(boostItem.orElse(null), power.orElse(null)));
    }
}
