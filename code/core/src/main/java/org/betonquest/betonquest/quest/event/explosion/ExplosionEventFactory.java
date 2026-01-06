package org.betonquest.betonquest.quest.event.explosion;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;

/**
 * Factory to create explosion events from {@link Instruction}s.
 */
public class ExplosionEventFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Create the explosion event factory.
     */
    public ExplosionEventFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createExplosionEvent(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createExplosionEvent(instruction);
    }

    private NullableActionAdapter createExplosionEvent(final Instruction instruction) throws QuestException {
        final Argument<Boolean> setsFire = instruction.parse("1"::equals).get();
        final Argument<Boolean> breaksBlocks = instruction.parse("1"::equals).get();
        final Argument<Number> power = instruction.number().get();
        final Argument<Location> location = instruction.location().get();
        return new NullableActionAdapter(new ExplosionEvent(location, power, setsFire, breaksBlocks));
    }
}
