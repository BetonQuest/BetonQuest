package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add class points to a player.
 */
public class MMOCoreClassPointsEvent implements PlayerEvent {

    /**
     * Amount to grant.
     */
    private final Argument<Number> amount;

    /**
     * Create a new class point add event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreClassPointsEvent(final Argument<Number> amount) {
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
