package org.betonquest.betonquest.quest.event.experience;

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

import java.util.Locale;
import java.util.Optional;

/**
 * Factory for the experience event.
 */
public class ExperienceEventFactory implements PlayerEventFactory {
    /**
     * Logger factory to create a logger for the events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the experience event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     * @param data          the data for primary server thread access
     */
    public ExperienceEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Number> amount = instruction.get(Argument.NUMBER);
        ExperienceModification experienceType = ExperienceModification.ADD_EXPERIENCE;
        String action = instruction.getValue("action");
        if (instruction.hasArgument("level")) {
            experienceType = ExperienceModification.ADD_LEVEL;
        } else if (action != null) {
            action = action.toUpperCase(Locale.ROOT);

            final Optional<ExperienceModification> modification = ExperienceModification.getFromInstruction(action);
            if (modification.isPresent()) {
                experienceType = modification.get();
            } else {
                throw new QuestException(action + " is not a valid experience modification type.");
            }
        }
        return new PrimaryServerThreadEvent(new OnlineEventAdapter(
                new ExperienceEvent(experienceType, amount),
                loggerFactory.create(ExperienceEvent.class),
                instruction.getPackage()
        ), data);
    }
}
