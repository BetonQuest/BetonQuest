package org.betonquest.betonquest.quest.action.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.Locale;
import java.util.Optional;

/**
 * Factory for the experience action.
 */
public class ExperienceActionFactory implements PlayerActionFactory {

    /**
     * Logger factory to create a logger for the actions.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the experience action factory.
     *
     * @param loggerFactory the logger factory to create a logger for the actions
     */
    public ExperienceActionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Number> amount = instruction.number().get();
        ExperienceModification experienceType = ExperienceModification.ADD_EXPERIENCE;
        final Optional<Argument<String>> action = instruction.string().get("action");
        String targetAction = action.isPresent() ? action.get().getValue(null) : null;
        final boolean level = instruction.bool().getFlag("level", true)
                .getValue(null).orElse(false);
        if (level) {
            experienceType = ExperienceModification.ADD_LEVEL;
        } else if (targetAction != null) {
            targetAction = targetAction.toUpperCase(Locale.ROOT);

            final Optional<ExperienceModification> modification = ExperienceModification.getFromInstruction(targetAction);
            if (modification.isPresent()) {
                experienceType = modification.get();
            } else {
                throw new QuestException(targetAction + " is not a valid experience modification type.");
            }
        }
        return new OnlineActionAdapter(new ExperienceAction(experienceType, amount),
                loggerFactory.create(ExperienceAction.class), instruction.getPackage());
    }
}
