package org.betonquest.betonquest.compatibility.effectlib.action;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Factory to create {@link ParticleAction}s from {@link Instruction}s.
 */
public class ParticleActionFactory implements PlayerActionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * Create a factory for particle actions.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param manager       the effect manager which will create and control the particles
     */
    public ParticleActionFactory(final BetonQuestLoggerFactory loggerFactory, final EffectManager manager) {
        this.loggerFactory = loggerFactory;
        this.manager = manager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.string().get().getValue(null);
        final ConfigurationSection parameters = Utils.getNN(instruction.getPackage().getConfig().getConfigurationSection("effects." + string),
                "Effect '" + string + "' does not exist!");
        final String rawEffectClass = Utils.getNN(parameters.getString("class"), "Effect '" + string + "' is incorrectly defined");
        final String effectClass = instruction.chainForArgument(rawEffectClass).string().get().getValue(null);
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final FlagArgument<Boolean> privateParticle = instruction.bool().getFlag("private", true);
        final ParticleAction particleAction = new ParticleAction(manager, effectClass, parameters, loc, privateParticle);
        return new OnlineActionAdapter(particleAction, loggerFactory.create(ParticleAction.class), instruction.getPackage());
    }
}
