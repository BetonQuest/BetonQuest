package org.betonquest.betonquest.quest.condition.experience;

import com.eteirnum.core.player.attributes.PlayerAttributeType;
import com.eteirnum.core.player.attributes.PlayerAttributesCalculator;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Requires the player to have specified level more.
 */
public class ExperienceCondition implements OnlineCondition {

    /**
     * The level the player needs to get.
     */
    private final VariableNumber amount;

    /**
     * Creates a new experience condition.
     *
     * @param amount The experience the player needs to get.
     */
    public ExperienceCondition(final VariableNumber amount) {
        this.amount = amount;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        final double amount = this.amount.getValue(profile).intValue();

        final Number lvl = PlayerAttributesCalculator.getTotalAttributeValue(profile.getPlayer(), PlayerAttributeType.EXP, true);

        return lvl.intValue() >= amount;
    }
}
