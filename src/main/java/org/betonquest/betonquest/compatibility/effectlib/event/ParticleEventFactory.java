package org.betonquest.betonquest.compatibility.effectlib.event;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Factory to create {@link ParticleEvent}s from {@link Instruction}s.
 */
public class ParticleEventFactory implements EventFactory {
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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String string = instruction.next();
        final ConfigurationSection parameters = Utils.getNN(instruction.getPackage().getConfig().getConfigurationSection("effects." + string),
                "Effect '" + string + "' does not exist!");
        final String effectClass = Utils.getNN(parameters.getString("class"), "Effect '" + string + "' is incorrectly defined");
        final VariableLocation loc = instruction.get(instruction.getOptional("loc"), VariableLocation::new);
        final boolean privateParticle = instruction.hasArgument("private");
        final ParticleEvent particleEvent = new ParticleEvent(manager, effectClass, parameters, loc, privateParticle);
        final Event event = new OnlineEventAdapter(particleEvent, loggerFactory.create(ParticleEvent.class), instruction.getPackage());
        return new PrimaryServerThreadEvent(event, data);
    }
}
