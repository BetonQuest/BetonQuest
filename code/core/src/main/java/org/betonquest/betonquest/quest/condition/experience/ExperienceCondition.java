package org.betonquest.betonquest.quest.condition.experience;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Requires the player to have specified level of experience or more.
 */
public class ExperienceCondition implements OnlineCondition {

    /**
     * The experience level the player needs to get, the decimal part is the percentage to the next level.
     */
    private final Argument<Number> amount;

    /**
     * Creates a new experience condition.
     *
     * @param amount the experience level the player needs to get, the decimal part is the percentage to the next level
     */
    public ExperienceCondition(final Argument<Number> amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final double amount = this.amount.getValue(profile).doubleValue();
        return profile.getPlayer().getLevel() + profile.getPlayer().getExp() >= amount;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
