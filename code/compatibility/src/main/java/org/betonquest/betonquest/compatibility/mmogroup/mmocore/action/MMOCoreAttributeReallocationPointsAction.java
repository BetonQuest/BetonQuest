package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Event to add attribute reallocation points to a player.
 */
public class MMOCoreAttributeReallocationPointsAction implements PlayerAction {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * Create a new attribute reallocation point event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreAttributeReallocationPointsAction(final Argument<Number> amount) {
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = this.amount.getValue(profile).intValue();
        data.giveAttributeReallocationPoints(amount);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
