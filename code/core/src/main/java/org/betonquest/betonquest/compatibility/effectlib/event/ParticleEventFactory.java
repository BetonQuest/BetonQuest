package org.betonquest.betonquest.compatibility.effectlib.event;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Effect manager which will create and control the particles.
     */
    private final EffectManager manager;

    /**
     * Create a factory for particle events.
     *
     * @param loggerFactory the logger factory to create new class specific logger
     * @param data          the data for primary server thread access
     * @param manager       the effect manager which will create and control the particles
     */
    public ParticleEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data, final EffectManager manager) {
        this.loggerFactory = loggerFactory;
        this.data = data;
        this.manager = manager;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String string = instruction.get(Argument.STRING).getValue(null);
        final ConfigurationSection parameters = Utils.getNN(instruction.getPackage().getConfig().getConfigurationSection("effects." + string),
                "Effect '" + string + "' does not exist!");
        final String rawEffectClass = Utils.getNN(parameters.getString("class"), "Effect '" + string + "' is incorrectly defined");
        final String effectClass = instruction.get(rawEffectClass, Argument.STRING).getValue(null);
        final Variable<Location> loc = instruction.getValue("loc", Argument.LOCATION);
        final boolean privateParticle = instruction.hasArgument("private");
        final ParticleEvent particleEvent = new ParticleEvent(manager, effectClass, parameters, loc, privateParticle);
        final PlayerEvent playerEvent = new OnlineEventAdapter(particleEvent, loggerFactory.create(ParticleEvent.class), instruction.getPackage());
        return new PrimaryServerThreadEvent(playerEvent, data);
    }
}
