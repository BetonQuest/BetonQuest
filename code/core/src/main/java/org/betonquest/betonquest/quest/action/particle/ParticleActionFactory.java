package org.betonquest.betonquest.quest.action.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

/**
 * Factory to create particle actions from {@link Instruction}s.
 */
public class ParticleActionFactory implements PlayerlessActionFactory, PlayerActionFactory {

    /**
     * Creates the particle action factory.
     */
    public ParticleActionFactory() {
    }

    private NullableActionAdapter parse(final Instruction instruction) throws QuestException {
        final Argument<Particle> particle = instruction.enumeration(Particle.class).get();
        final Argument<Location> location = instruction.location().get();
        final Argument<Number> count = instruction.number().get("count", 1);
        final Argument<Vector> offset = instruction.vector().get("offset", new Vector(0, 0, 0));
        final Argument<Number> range = instruction.number().get("range", 32);
        return new NullableActionAdapter(new ParticleAction(particle, location, count, offset, range));
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parse(instruction);
    }
}
