package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

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
    private final Variable<Number> amount;

    /**
     * Creates a new experience event.
     *
     * @param experienceModification the modification to apply
     * @param amount                 the amount of experience
     */
    public ExperienceEvent(final ExperienceModification experienceModification, final Variable<Number> amount) {
        this.experienceModification = experienceModification;
        this.amount = amount;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        experienceModification.apply(profile.getPlayer(), amount.getValue(profile).floatValue());
    }
}
