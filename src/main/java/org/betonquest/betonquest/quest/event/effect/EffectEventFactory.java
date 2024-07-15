package org.betonquest.betonquest.quest.event.effect;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.OnlineProfileRequiredEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Factory to create effect events from {@link Instruction}s.
 */
public class EffectEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the effect event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public EffectEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        final PotionEffectType effect = PotionEffectType.getByName(instruction.next());
        if (effect == null) {
            throw new InstructionParseException("Unknown effect type: " + instruction.current());
        }
        try {
            final VariableNumber duration = instruction.getVarNum();
            final VariableNumber level = instruction.getVarNum();
            final boolean ambient = instruction.hasArgument("ambient");
            final boolean hidden = instruction.hasArgument("hidden");
            final boolean icon = !instruction.hasArgument("noicon");
            return new PrimaryServerThreadEvent(new OnlineProfileRequiredEvent(
                    loggerFactory.create(EffectEvent.class), new EffectEvent(effect, duration, level, ambient, hidden, icon), instruction.getPackage()),
                    data);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not parse effect duration and amplifier", e);
        }
    }
}
