package org.betonquest.betonquest.quest.event.experience;

import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

/**
 * Gives the player specified amount of experience.
 */
public class ExperienceEvent implements Event {

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
     * @param amount         the amount of experience
     */
    public ExperienceEvent(final ExperienceModification experienceModification, final VariableNumber amount) {
        this.experienceModification = experienceModification;
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        experienceModification.apply(profile.getOnlineProfile().get().getPlayer(), (float) amount.getDouble(profile));
    }
}
