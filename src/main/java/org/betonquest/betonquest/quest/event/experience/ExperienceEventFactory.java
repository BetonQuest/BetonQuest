package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

import java.util.Locale;
import java.util.Optional;

/**
 * Factory for the experience event.
 */
public class ExperienceEventFactory implements EventFactory {
    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the experience event factory.
     *
     * @param loggerFactory logger factory to use
     * @param data          the data for primary server thread access
     */
    public ExperienceEventFactory(final BetonQuestLoggerFactory loggerFactory, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.data = data;
    }

    @Override
    @SuppressWarnings("PMD.PrematureDeclaration")
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final VariableNumber amount = instruction.getVarNum();
        ExperienceModification experienceType = ExperienceModification.ADD_EXPERIENCE;
        String action = instruction.getOptional("action");
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
