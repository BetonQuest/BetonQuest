package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

/**
 * Factory for {@link LightningEvent} from the {@link Instruction}.
 */
public class LightningEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create a new LightningEventFactory.
     */
    public LightningEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createLightningEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createLightningEvent(instruction);
    }

    private NullableEventAdapter createLightningEvent(final Instruction instruction) throws QuestException {
        final Argument<Location> location = instruction.location().get();
        final FlagArgument<Boolean> noDamage = instruction.bool().getFlag("noDamage", false);
        return new NullableEventAdapter(new LightningEvent(location, noDamage));
    }
}
