package org.betonquest.betonquest.quest.condition.mana;

import com.eteirnum.core.player.attributes.PlayerAttributeType;
import com.eteirnum.core.player.attributes.PlayerAttributesCalculator;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * A condition that checks if the player's mana amount is at a certain level.
 */
public class ManaCondition implements OnlineCondition {

    /**
     * The hunger level required to pass the condition.
     */
    private final VariableNumber mana;

    /**
     * Create a new mana condition.
     *
     * @param mana the mana amount required to pass the condition
     */
    public ManaCondition(final VariableNumber mana) {
        this.mana = mana;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestRuntimeException {
        return PlayerAttributesCalculator.getTotalAttributeValue(profile.getPlayer(), PlayerAttributeType.MANA, true).intValue()
                >= mana.getValue(profile).doubleValue();
    }
}
