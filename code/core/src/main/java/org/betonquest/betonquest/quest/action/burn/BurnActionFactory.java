package org.betonquest.betonquest.quest.action.burn;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.lib.argument.type.TimeUnit;

/**
 * Factory to create burn actions from {@link Instruction}s.
 */
public class BurnActionFactory implements PlayerActionFactory {

    /**
     * Create the burn action factory.
     */
    public BurnActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> duration = instruction.number().get();
        final Argument<TimeUnit> timeUnit = instruction.enumeration(TimeUnit.class).get("unit", TimeUnit.SECONDS);
        return new OnlineActionAdapter(new BurnAction(duration, timeUnit));
    }
}
