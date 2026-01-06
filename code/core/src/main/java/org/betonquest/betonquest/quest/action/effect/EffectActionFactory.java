package org.betonquest.betonquest.quest.action.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.online.OnlineActionAdapter;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory to create effect events from {@link Instruction}s.
 */
public class EffectActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the effect event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public EffectActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final PotionEffectType effect = PotionEffectType.getByName(instruction.string().get().getValue(null));
        if (effect == null) {
            throw new QuestException("Unknown effect type: " + instruction.current());
        }
        try {
            final Argument<Number> duration = instruction.number().get();
            final Argument<Number> level = instruction.number().get();
            final FlagArgument<Boolean> ambient = instruction.bool().getFlag("ambient", true);
            final FlagArgument<Boolean> hidden = instruction.bool().getFlag("hidden", true);
            final FlagArgument<Boolean> noicon = instruction.bool().getFlag("noicon", true);
            return new OnlineActionAdapter(new EffectAction(effect, duration, level, ambient, hidden, noicon),
                    loggerFactory.create(EffectAction.class), instruction.getPackage());
        } catch (final QuestException e) {
            throw new QuestException("Could not parse effect duration and amplifier", e);
        }
    }
}
