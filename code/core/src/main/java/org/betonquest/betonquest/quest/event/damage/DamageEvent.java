package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;

/**
 * The damage event. It damages the player.
 */
public class DamageEvent implements OnlineEvent {

    /**
     * The amount of damage to inflict.
     */
    private final Argument<Number> damage;

    /**
     * Create a damage event that inflicts the given amount of damage to the player.
     *
     * @param damage the amount of damage to inflict
     */
    public DamageEvent(final Argument<Number> damage) {
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
