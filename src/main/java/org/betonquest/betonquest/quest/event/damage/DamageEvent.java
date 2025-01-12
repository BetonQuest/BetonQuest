package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * The damage event. It damages the player.
 */
public class DamageEvent implements OnlineEvent {
    /**
     * Amount of damage to inflict.
     */
    private final VariableNumber damage;

    /**
     * Create a damage event that inflicts the given amount of damage to the player.
     *
     * @param damage damage to inflict
     */
    public DamageEvent(final VariableNumber damage) {
        this.damage = damage;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final double calculatedDamage = Math.abs(damage.getValue(profile).doubleValue());
        profile.getPlayer().damage(calculatedDamage);
    }
}
