package org.betonquest.betonquest.quest.event.lightning;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;

/**
 * Factory for {@link LightningEvent} from the {@link Instruction}.
 */
public class LightningEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new LightningEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public LightningEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createLightningEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createLightningEvent(instruction), data);
    }

    private NullableEventAdapter createLightningEvent(final Instruction instruction) throws QuestException {
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final boolean noDamage = instruction.hasArgument("noDamage");
        return new NullableEventAdapter(new LightningEvent(location, noDamage));
    }
}
