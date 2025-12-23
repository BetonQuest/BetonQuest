package org.betonquest.betonquest.quest.condition.hunger;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;

/**
 * A condition that checks if the player's hunger level is at a certain level.
 */
public class HungerCondition implements OnlineCondition {

    /**
     * The hunger level required to pass the condition.
     */
    private final Variable<Number> hunger;

    /**
     * Create a new hunger condition.
     *
     * @param hunger the hunger level required to pass the condition
     */
    public HungerCondition(final Variable<Number> hunger) {
        this.hunger = hunger;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        return profile.getPlayer().getFoodLevel() >= hunger.getValue(profile).doubleValue();
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
