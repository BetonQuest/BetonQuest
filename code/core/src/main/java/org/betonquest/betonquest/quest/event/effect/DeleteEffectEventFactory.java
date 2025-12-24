package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
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
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the delete effect event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param data          the data for primary server thread access
     */
    public DeleteEffectEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<List<PotionEffectType>> effects;
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
            effects = new VariableList<>(Collections.emptyList());
        }
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new DeleteEffectEvent(effects),
                loggerFactory.create(DeleteEffectEvent.class),
                instruction.getPackage()
        ), data);
    }
}
