package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Gives the player specified amount of experience.
 */
public class ExperienceEvent implements OnlineEvent {

    /**
     * The modification to apply.
     */
    private final ExperienceModification experienceModification;

    /**
     * The amount of experience or level.
     */
    private final VariableNumber amount;

    /**
     * Creates a new experience event.
     *
     * @param experienceModification the modification to apply
     * @param amount                 the amount of experience
     */
    public ExperienceEvent(final ExperienceModification experienceModification, final VariableNumber amount) {
        this.experienceModification = experienceModification;
        this.amount = amount;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        experienceModification.apply(profile.getPlayer(), amount.getValue(profile).floatValue());
    }
}
