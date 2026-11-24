package org.betonquest.betonquest.quest.action.particle;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ParticleIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.particle.Particles;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArguments;
import org.betonquest.betonquest.quest.action.ThrowExceptionPlayerlessAction;
import org.bukkit.Location;

/**
 * Factory for {@link SpawnParticlesAction}.
 */
public class SpawnParticlesActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The particle service.
     */
    private final Particles particles;

    /**
     * Create a new spawn particles action factory.
     *
     * @param particles the particle service
     */
    public SpawnParticlesActionFactory(final Particles particles) {
        this.particles = particles;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerAction action = createAction(instruction);
        if (!instruction.hasOptional("loc")) {
            return new OnlineActionAdapter(action::execute);
        }
        return action;
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        if (!instruction.hasOptional("loc")) {
            return new ThrowExceptionPlayerlessAction("SpawnParticles action requires 'loc' argument in an independent context");
        }
        return createAction(instruction);
    }

    private NullableActionAdapter createAction(final Instruction instruction) throws QuestException {
        final Argument<ParticleIdentifier> identifier = instruction.identifier(ParticleIdentifier.class).get();
        final Argument<Location> location = instruction.location().get("loc").orElse(DefaultArguments.PLAYER_LOCATION);
        return new NullableActionAdapter(new SpawnParticlesAction(particles, identifier, location));
    }
}
