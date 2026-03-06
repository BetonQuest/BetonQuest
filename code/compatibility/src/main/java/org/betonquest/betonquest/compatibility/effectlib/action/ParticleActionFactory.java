package org.betonquest.betonquest.compatibility.effectlib.action;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Factory to create {@link ParticleAction}s from {@link Instruction}s.
 */
public class ParticleActionFactory implements PlayerActionFactory {

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * Create a factory for particle actions.
     *
     * @param manager the effect manager which will create and control the particles
     */
    public ParticleActionFactory(final EffectManager manager) {
        this.manager = manager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.string().get().getValue(null);
        final ConfigurationSection parameters = instruction.getPackage().getConfig().getConfigurationSection("effects." + string);
        if (parameters == null) {
            throw new QuestException("Effect '%s' does not exist!".formatted(string));
        }
        final String rawEffectClass = parameters.getString("class");
        if (rawEffectClass == null) {
            throw new QuestException("Effect '%s' does not have a class!".formatted(string));
        }
        final String effectClass = instruction.chainForArgument(rawEffectClass).string().get().getValue(null);
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final FlagArgument<Boolean> privateParticle = instruction.bool().getFlag("private", true);
        final ParticleAction particleAction = new ParticleAction(manager, effectClass, parameters, loc, privateParticle);
        return new OnlineActionAdapter(particleAction);
    }
}
