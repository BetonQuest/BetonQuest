package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;

/**
 * Factory to create explosion events from {@link Instruction}s.
 */
public class ExplosionEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Create the explosion event factory.
     */
    public ExplosionEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createExplosionEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createExplosionEvent(instruction);
    }

    private NullableEventAdapter createExplosionEvent(final Instruction instruction) throws QuestException {
        final Variable<Boolean> setsFire = instruction.parse("1"::equals).get();
        final Variable<Boolean> breaksBlocks = instruction.parse("1"::equals).get();
        final Variable<Number> power = instruction.number().get();
        final Variable<Location> location = instruction.location().get();
        return new NullableEventAdapter(new ExplosionEvent(location, power, setsFire, breaksBlocks));
    }
}
