package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Action to add class points to a player.
 */
public class MMOCoreClassPointsAction implements PlayerAction {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * Create a new class point add action.
     *
     * @param amount the amount to grant
     */
    public MMOCoreClassPointsAction(final Argument<Number> amount) {
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = this.amount.getValue(profile).intValue();
        data.giveClassPoints(amount);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
