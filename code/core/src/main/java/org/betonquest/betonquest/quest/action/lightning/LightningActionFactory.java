package org.betonquest.betonquest.quest.action.lightning;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

/**
 * Factory for {@link LightningAction} from the {@link Instruction}.
 */
public class LightningActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create a new LightningActionFactory.
     */
    public LightningActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createLightningEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createLightningEvent(instruction);
    }

    private NullableActionAdapter createLightningEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> noDamage = instruction.bool().getFlag("noDamage", true);
        return new NullableActionAdapter(new LightningAction(location, noDamage));
    }
}
