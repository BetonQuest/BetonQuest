package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;

/**
 * Action to add attribute reallocation points to a player.
 */
public class MMOCoreAttributeReallocationPointsAction implements OnlineAction {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * Create a new attribute reallocation point action.
     *
     * @param amount the amount to grant
     */
    public MMOCoreAttributeReallocationPointsAction(final Argument<Number> amount) {
        this.amount = amount;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = this.amount.getValue(profile).intValue();
        data.giveAttributeReallocationPoints(amount);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
