package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.DefaultListArgument;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create delete effect events from {@link Instruction}s.
 */
public class DeleteEffectEventFactory implements PlayerEventFactory {

    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the delete effect event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public DeleteEffectEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<PotionEffectType>> effects;
        if (!instruction.hasArgument("any") && instruction.size() > 1) {
            effects = instruction.parse(type -> {
                final PotionEffectType effect = PotionEffectType.getByName(type);
                if (effect == null) {
                    throw new QuestException("Unknown effect type: " + type);
                } else {
                    return effect;
                }
            }).getList();
        } else {
            effects = new DefaultListArgument<>(Collections.emptyList());
        }
        return new OnlineEventAdapter(new DeleteEffectEvent(effects),
                loggerFactory.create(DeleteEffectEvent.class), instruction.getPackage());
    }
}
