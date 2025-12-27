package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory to create effect events from {@link Instruction}s.
 */
public class EffectEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the effect event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public EffectEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final PotionEffectType effect = PotionEffectType.getByName(instruction.string().get().getValue(null));
        if (effect == null) {
            throw new QuestException("Unknown effect type: " + instruction.current());
        }
        try {
            final Argument<Number> duration = instruction.number().get();
            final Argument<Number> level = instruction.number().get();
            final FlagArgument<Boolean> ambient = instruction.bool().getFlag("ambient", false);
            final FlagArgument<Boolean> hidden = instruction.bool().getFlag("hidden", false);
            final FlagArgument<Boolean> noicon = instruction.bool().getFlag("noicon", false);
            return new OnlineEventAdapter(new EffectEvent(effect, duration, level, ambient, hidden, noicon),
                    loggerFactory.create(EffectEvent.class), instruction.getPackage());
        } catch (final QuestException e) {
            throw new QuestException("Could not parse effect duration and amplifier", e);
        }
    }
}
