package org.betonquest.betonquest.quest.condition.health;

import com.eteirnum.core.player.attributes.PlayerAttributeType;
import com.eteirnum.core.player.attributes.PlayerAttributesCalculator;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final double expectedHealth = health.getValue(profile).doubleValue();

        final Number hp = PlayerAttributesCalculator.getTotalAttributeValue(profile.getPlayer(), PlayerAttributeType.HP, true);

        return hp.floatValue() >= expectedHealth;
    }
}
