package org.betonquest.betonquest.quest.action.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * Gives the player specified amount of experience.
 */
public class ExperienceAction implements OnlineAction {

    /**
     * The modification to apply.
     */
    private final ExperienceModification experienceModification;

    /**
     * The amount of experience or level.
     */
    private final Argument<Number> amount;

    /**
     * Creates a new experience action.
     *
     * @param experienceModification the modification to apply
     * @param amount                 the amount of experience
     */
    public ExperienceAction(final ExperienceModification experienceModification, final Argument<Number> amount) {
        this.experienceModification = experienceModification;
        this.amount = amount;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        experienceModification.apply(profile.getPlayer(), amount.getValue(profile).floatValue());
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
