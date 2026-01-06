package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;

/**
 * Gives the player specified amount of experience.
 */
public class ExperienceEvent implements OnlineAction {

    /**
     * The modification to apply.
     */
    private final ExperienceModification experienceModification;

    /**
     * The amount of experience or level.
     */
    private final Argument<Number> amount;

    /**
     * Creates a new experience event.
     *
     * @param experienceModification the modification to apply
     * @param amount                 the amount of experience
     */
    public ExperienceEvent(final ExperienceModification experienceModification, final Argument<Number> amount) {
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
