package org.betonquest.betonquest.compatibility.effectlib.event;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Factory to create {@link ParticleEvent}s from {@link Instruction}s.
 */
public class ParticleEventFactory implements PlayerEventFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * Create a factory for particle events.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param manager       the effect manager which will create and control the particles
     */
    public ParticleEventFactory(final BetonQuestLoggerFactory loggerFactory, final EffectManager manager) {
        this.loggerFactory = loggerFactory;
        this.manager = manager;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.string().get().getValue(null);
        final ConfigurationSection parameters = Utils.getNN(instruction.getPackage().getConfig().getConfigurationSection("effects." + string),
                "Effect '" + string + "' does not exist!");
        final String rawEffectClass = Utils.getNN(parameters.getString("class"), "Effect '" + string + "' is incorrectly defined");
        final String effectClass = instruction.chainForArgument(rawEffectClass).string().get().getValue(null);
        final Argument<Location> loc = instruction.location().get("loc").orElse(null);
        final FlagArgument<Boolean> privateParticle = instruction.bool().getFlag("private", true);
        final ParticleEvent particleEvent = new ParticleEvent(manager, effectClass, parameters, loc, privateParticle);
        return new OnlineEventAdapter(particleEvent, loggerFactory.create(ParticleEvent.class), instruction.getPackage());
    }
}
