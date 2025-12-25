package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.online.OnlineEventAdapter;

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
     * Create the experience event factory.
     *
     * @param loggerFactory the logger factory to create a logger for the events
     */
    public ExperienceEventFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        ExperienceModification experienceType = ExperienceModification.ADD_EXPERIENCE;
        final Optional<Argument<String>> actionVariable = instruction.string().get("action");
        String action = actionVariable.isPresent() ? actionVariable.get().getValue(null) : null;
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
        return new OnlineEventAdapter(new ExperienceEvent(experienceType, amount),
                loggerFactory.create(ExperienceEvent.class), instruction.getPackage());
    }
}
