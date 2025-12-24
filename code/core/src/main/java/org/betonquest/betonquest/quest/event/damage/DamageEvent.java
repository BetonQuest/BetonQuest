package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * The damage event. It damages the player.
 */
public class DamageEvent implements OnlineEvent {

    /**
     * Amount of damage to inflict.
     */
    private final Variable<Number> damage;

    /**
     * Create a damage event that inflicts the given amount of damage to the player.
     *
     * @param damage damage to inflict
     */
    public DamageEvent(final Variable<Number> damage) {
        this.damage = damage;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final double calculatedDamage = Math.abs(damage.getValue(profile).doubleValue());
        profile.getPlayer().damage(calculatedDamage);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
