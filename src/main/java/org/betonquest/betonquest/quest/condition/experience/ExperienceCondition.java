package org.betonquest.betonquest.quest.condition.experience;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Requires the player to have specified level of experience or more.
 */
public class ExperienceCondition implements OnlineCondition {

    /**
     * The experience level the player needs to get.
     * The decimal part of the number is a percentage of the next level.
     */
    private final VariableNumber amount;

    /**
     * Creates a new experience condition.
     *
     * @param amount The experience level the player needs to get.
     */
    public ExperienceCondition(final VariableNumber amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final double amount = this.amount.getValue(profile).doubleValue();
        return profile.getPlayer().getLevel() + profile.getPlayer().getExp() >= amount;
    }
}
