package org.betonquest.betonquest.quest.condition.health;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * Requires the player to have specified amount of health (or more).
 */
public class HealthCondition implements OnlineCondition {

    /**
     * The health value.
     */
    private final Argument<Number> health;

    /**
     * Creates a new health condition.
     *
     * @param health the health value
     */
    public HealthCondition(final Argument<Number> health) {
        this.health = health;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final double expectedHealth = health.getValue(profile).doubleValue();
        return profile.getPlayer().getHealth() >= expectedHealth;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
