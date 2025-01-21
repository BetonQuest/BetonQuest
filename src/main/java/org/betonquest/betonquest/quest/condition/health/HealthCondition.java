package org.betonquest.betonquest.quest.condition.health;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Requires the player to have specified amount of health (or more).
 */
public class HealthCondition implements OnlineCondition {

    /**
     * The health value.
     */
    private final VariableNumber health;

    /**
     * Creates a new health condition.
     *
     * @param health The health value
     */
    public HealthCondition(final VariableNumber health) {
        this.health = health;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final double expectedHealth = health.getValue(profile).doubleValue();
        return profile.getPlayer().getHealth() >= expectedHealth;
    }
}
