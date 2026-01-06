package org.betonquest.betonquest.compatibility.mmogroup.mmocore.action;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.action.PlayerAction;

/**
 * Event to add attribute points to a player.
 */
public class MMOCoreAttributePointsAction implements PlayerAction {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * Create a new attribute point event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreAttributePointsAction(final Argument<Number> amount) {
        this.amount = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = this.amount.getValue(profile).intValue();
        data.giveAttributePoints(amount);
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
