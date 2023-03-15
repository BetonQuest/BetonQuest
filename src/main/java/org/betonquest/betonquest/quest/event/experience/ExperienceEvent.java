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
     * The experience type.
     */
    private final Experience experienceType;

    /**
     * The amount of experience or level.
     */
    private final VariableNumber amount;

    /**
     * Creates a new experience event.
     *
     * @param experienceType the experience type
     * @param amount         the amount of experience
     */
    public ExperienceEvent(final Experience experienceType, final VariableNumber amount) {
        this.experienceType = experienceType;
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        experienceType.applyExperience(profile.getOnlineProfile().get().getPlayer(), (float) amount.getDouble(profile));
    }
}
