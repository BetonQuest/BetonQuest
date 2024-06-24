package org.betonquest.betonquest.quest.event.damage;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * The damage event. It damages the player.
 */
public class DamageEvent implements Event {
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
    public void execute(final Profile profile) throws QuestRuntimeException {
        final double calculatedDamage = Math.abs(damage.getDouble(profile));
        profile.getOnlineProfile()
                .map(OnlineProfile::getPlayer)
                .ifPresent(player -> player.damage(calculatedDamage));
    }
}
