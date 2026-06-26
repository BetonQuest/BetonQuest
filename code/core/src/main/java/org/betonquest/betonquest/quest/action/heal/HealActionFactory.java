package org.betonquest.betonquest.quest.action.heal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create heal actions from {@link Instruction}s.
 */
public class HealActionFactory implements PlayerActionFactory {

    /**
     * Create the heal action factory.
     */
    public HealActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> healAmount = instruction.number().get();
        final Argument<HealOperation> operation = instruction.enumeration(HealOperation.class).get("operation", HealOperation.ADD);
        return new OnlineActionAdapter(new HealAction(healAmount, operation));
    }
}
